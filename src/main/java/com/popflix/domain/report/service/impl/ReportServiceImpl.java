package com.popflix.domain.report.service.impl;

import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.photoreview.entity.PhotoReview;
import com.popflix.domain.photoreview.entity.PhotoReviewComment;
import com.popflix.domain.photoreview.entity.PhotoReviewReply;
import com.popflix.domain.photoreview.exception.PhotoReviewCommentNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewReplyNotFoundException;
import com.popflix.domain.photoreview.repository.PhotoReviewCommentRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewReplyRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewRepository;
import com.popflix.domain.report.dto.AdminReportResponseDto;
import com.popflix.domain.report.dto.ReportDetailResponseDto;
import com.popflix.domain.report.dto.ReportPostDto;
import com.popflix.domain.report.entity.Report;
import com.popflix.domain.report.enums.ReportStatus;
import com.popflix.domain.report.enums.ReportTarget;
import com.popflix.domain.report.exception.ReportNotFoundException;
import com.popflix.domain.report.repository.ReportRepository;
import com.popflix.domain.report.service.ReportService;
import com.popflix.domain.review.entity.Comment;
import com.popflix.domain.review.entity.Review;
import com.popflix.domain.review.exception.CommentNotFoundException;
import com.popflix.domain.review.exception.ReviewNotFoundException;
import com.popflix.domain.review.repository.CommentRepository;
import com.popflix.domain.review.repository.ReviewRepository;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final PhotoReviewRepository photoReviewRepository;
    private final PhotoReviewCommentRepository photoReviewCommentRepository;
    private final PhotoReviewReplyRepository photoReviewReplyRepository;

    @Override
    @Transactional
    public ReportDetailResponseDto createReport(ReportPostDto requestDto) {
        // 신고자 확인
        User reporter = userRepository.findById(requestDto.getReporterId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getReporterId()));

        // 중복 신고 체크
        if (reportRepository.existsActiveByReporterIdAndTargetTypeAndTargetId(
                requestDto.getReporterId(),
                requestDto.getTargetType(),
                requestDto.getTargetId()
        )) {
            throw new IllegalStateException("이미 신고한 컨텐츠입니다.");
        }

        // 신고 생성
        Report report = Report.builder()
                .reporter(reporter)
                .targetType(requestDto.getTargetType())
                .targetId(requestDto.getTargetId())
                .reason(requestDto.getReason())
                .build();

        Report savedReport = reportRepository.save(report);
        return convertToReportDetailResponse(savedReport);
    }

    @Override
    public ReportDetailResponseDto getReport(Long reportId) {
        Report report = reportRepository.findActiveById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        return convertToReportDetailResponse(report);
    }

    @Override
    public Page<AdminReportResponseDto> getAdminReports(ReportStatus status, Pageable pageable) {
        return reportRepository.findPageActiveByStatus(status, pageable)
                .map(this::convertToAdminReportResponse);
    }

    @Override
    @Transactional
    public void updateReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findActiveById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        report.updateStatus(status);

        if (status == ReportStatus.ACCEPTED) {
            hideTargetContent(report);
        }
    }

    private AdminReportResponseDto convertToAdminReportResponse(Report report) {
        User targetAuthor = getTargetAuthor(report);
        return AdminReportResponseDto.builder()
                .reportId(report.getId())
                .reporterId(report.getReporter().getUserId())
                .reporterNickname(report.getReporter().getNickname())
                .reporterEmail(report.getReporter().getEmail())
                .authorId(targetAuthor.getUserId())
                .authorNickname(targetAuthor.getNickname())
                .authorEmail(targetAuthor.getEmail())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .targetContent(getTargetContent(report))
                .status(report.getStatus())
                .reportedAt(report.getCreateAt())
                .navigationUrl(generateNavigationUrl(report))
                .build();
    }

    private ReportDetailResponseDto convertToReportDetailResponse(Report report) {
        return ReportDetailResponseDto.builder()
                .reportId(report.getId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .status(report.getStatus())
                .reporter(convertToReportUserInfo(report.getReporter()))
                .content(getReportContentInfo(report))
                .createdAt(report.getCreateAt())
                .build();
    }

    private void hideTargetContent(Report report) {
        switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findById(report.getTargetId())
                    .ifPresent(Review::hide);
            case COMMENT -> commentRepository.findById(report.getTargetId())
                    .ifPresent(Comment::hide);
            case PHOTO_REVIEW ->
                    photoReviewRepository.findById(report.getTargetId())
                            .ifPresent(PhotoReview::hide);
            case PHOTO_REVIEW_COMMENT ->
                    photoReviewCommentRepository.findById(report.getTargetId())
                            .ifPresent(PhotoReviewComment::hide);
            case PHOTO_REVIEW_REPLY ->
                    photoReviewReplyRepository.findById(report.getTargetId())
                            .ifPresent(PhotoReviewReply::hide);
        }
    }

    private String getTargetContent(Report report) {
        return switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findById(report.getTargetId())
                    .map(Review::getReview)
                    .orElse("삭제된 리뷰입니다.");
            case COMMENT -> commentRepository.findById(report.getTargetId())
                    .map(Comment::getComment)
                    .orElse("삭제된 댓글입니다.");
            case PHOTO_REVIEW ->
                    photoReviewRepository.findById(report.getTargetId())
                            .map(PhotoReview::getReview)
                            .orElse("삭제된 포토리뷰입니다.");
            case PHOTO_REVIEW_COMMENT ->
                    photoReviewCommentRepository.findById(report.getTargetId())
                            .map(PhotoReviewComment::getComment)
                            .orElse("삭제된 포토리뷰 댓글입니다.");
            case PHOTO_REVIEW_REPLY ->
                    photoReviewReplyRepository.findById(report.getTargetId())
                            .map(PhotoReviewReply::getReply)
                            .orElse("삭제된 포토리뷰 대댓글입니다.");
        };
    }

    private User getTargetAuthor(Report report) {
        return switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findById(report.getTargetId())
                    .map(Review::getUser)
                    .orElseThrow(() -> new ReviewNotFoundException(report.getTargetId()));
            case COMMENT -> commentRepository.findById(report.getTargetId())
                    .map(Comment::getUser)
                    .orElseThrow(() -> new CommentNotFoundException(report.getTargetId()));
            case PHOTO_REVIEW ->
                    photoReviewRepository.findById(report.getTargetId())
                            .map(PhotoReview::getUser)
                            .orElseThrow(() -> new PhotoReviewNotFoundException(report.getTargetId()));
            case PHOTO_REVIEW_COMMENT ->
                    photoReviewCommentRepository.findById(report.getTargetId())
                            .map(PhotoReviewComment::getUser)
                            .orElseThrow(() -> new PhotoReviewCommentNotFoundException(report.getTargetId()));
            case PHOTO_REVIEW_REPLY ->
                    photoReviewReplyRepository.findById(report.getTargetId())
                            .map(PhotoReviewReply::getUser)
                            .orElseThrow(() -> new PhotoReviewReplyNotFoundException(report.getTargetId()));
        };
    }

    private String generateNavigationUrl(Report report) {
        return switch (report.getTargetType()) {
            case REVIEW -> String.format("/movies/%d/reviews/%d",
                    getMovieId(report), report.getTargetId());
            case COMMENT -> String.format("/movies/%d/reviews/%d#comment-%d",
                    getMovieId(report), getReviewId(report), report.getTargetId());
            case PHOTO_REVIEW -> String.format("/movies/%d/photo-reviews/%d",
                    getMovieId(report), report.getTargetId());
            case PHOTO_REVIEW_COMMENT ->
                    String.format("/movies/%d/photo-reviews/%d#comment-%d",
                            getMovieId(report), getPhotoReviewId(report), report.getTargetId());
            case PHOTO_REVIEW_REPLY ->
                    String.format("/movies/%d/photo-reviews/%d#reply-%d",
                            getMovieId(report), getPhotoReviewId(report), report.getTargetId());
        };
    }

    private Long getMovieId(Report report) {
        return switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findById(report.getTargetId())
                    .map(review -> review.getMovie().getId())
                    .orElse(null);
            case COMMENT -> commentRepository.findById(report.getTargetId())
                    .map(comment -> comment.getReview().getMovie().getId())
                    .orElse(null);
            case PHOTO_REVIEW ->
                    photoReviewRepository.findById(report.getTargetId())
                            .map(review -> review.getMovie().getId())
                            .orElse(null);
            case PHOTO_REVIEW_COMMENT ->
                    photoReviewCommentRepository.findById(report.getTargetId())
                            .map(comment -> comment.getPhotoReview().getMovie().getId())
                            .orElse(null);
            case PHOTO_REVIEW_REPLY ->
                    photoReviewReplyRepository.findById(report.getTargetId())
                            .map(reply -> reply.getComment().getPhotoReview().getMovie().getId())
                            .orElse(null);
        };
    }

    private Long getReviewId(Report report) {
        if (report.getTargetType() == ReportTarget.COMMENT) {
            return commentRepository.findById(report.getTargetId())
                    .map(comment -> comment.getReview().getReviewId())
                    .orElse(null);
        }
        return null;
    }

    private Long getPhotoReviewId(Report report) {
        return switch (report.getTargetType()) {
            case PHOTO_REVIEW_COMMENT ->
                    photoReviewCommentRepository.findById(report.getTargetId())
                            .map(comment -> comment.getPhotoReview().getReviewId())
                            .orElse(null);
            case PHOTO_REVIEW_REPLY ->
                    photoReviewReplyRepository.findById(report.getTargetId())
                            .map(reply -> reply.getComment().getPhotoReview().getReviewId())
                            .orElse(null);
            default -> null;
        };
    }

    private ReportDetailResponseDto.ReportUserInfo convertToReportUserInfo(User user) {
        return ReportDetailResponseDto.ReportUserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

    private ReportDetailResponseDto.ReportContentInfo getReportContentInfo(Report report) {
        return ReportDetailResponseDto.ReportContentInfo.builder()
                .content(getTargetContent(report))
                .createdAt(getTargetCreatedAt(report))
                .url(generateNavigationUrl(report))
                .build();
    }

    private LocalDateTime getTargetCreatedAt(Report report) {
        return switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findById(report.getTargetId())
                    .map(Review::getCreateAt)
                    .orElse(null);
            case COMMENT -> commentRepository.findById(report.getTargetId())
                    .map(Comment::getCreateAt)
                    .orElse(null);
            case PHOTO_REVIEW ->
                    photoReviewRepository.findById(report.getTargetId())
                            .map(PhotoReview::getCreateAt)
                            .orElse(null);
            case PHOTO_REVIEW_COMMENT ->
                    photoReviewCommentRepository.findById(report.getTargetId())
                            .map(PhotoReviewComment::getCreateAt)
                            .orElse(null);
            case PHOTO_REVIEW_REPLY ->
                    photoReviewReplyRepository.findById(report.getTargetId())
                            .map(PhotoReviewReply::getCreateAt)
                            .orElse(null);
        };
    }
}