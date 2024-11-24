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
import com.popflix.domain.report.dto.ReportDetailResponseDto;
import com.popflix.domain.report.dto.ReportListResponseDto;
import com.popflix.domain.report.dto.ReportPostDto;
import com.popflix.domain.report.dto.ReportResponseDto;
import com.popflix.domain.report.entity.Report;
import com.popflix.domain.report.enums.ReportStatus;
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

import java.util.Base64;
import java.util.List;

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
    public ReportResponseDto createReport(ReportPostDto requestDto) {
        User reporter = userRepository.findById(requestDto.getReporterId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getReporterId()));

        if (reportRepository.existsActiveByReporterIdAndTargetTypeAndTargetId(
                requestDto.getReporterId(),
                requestDto.getTargetType(),
                requestDto.getTargetId()
        )) {
            throw new IllegalStateException("이미 신고한 컨텐츠입니다.");
        }

        Report report = Report.builder()
                .reporter(reporter)
                .targetType(requestDto.getTargetType())
                .targetId(requestDto.getTargetId())
                .reason(requestDto.getReason())
                .build();

        Report savedReport = reportRepository.save(report);

        return convertToReportResponse(savedReport);
    }

    @Override
    public ReportDetailResponseDto getReport(Long reportId) {
        Report report = reportRepository.findActiveById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        return convertToReportDetailResponse(report);
    }

    @Override
    public Page<ReportListResponseDto> getReports(ReportStatus status, Pageable pageable) {
        Page<Report> reports = reportRepository.findPageActiveByStatus(status, pageable);
        long totalCount = reportRepository.countActiveByStatus(status);
        long pendingCount = reportRepository.countActiveByStatus(ReportStatus.PENDING);

        return reports.map(report ->
                ReportListResponseDto.builder()
                        .reports(List.of(convertToReportResponse(report)))
                        .totalCount(totalCount)
                        .pendingCount(pendingCount)
                        .build()
        );
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

    private ReportResponseDto convertToReportResponse(Report report) {
        return ReportResponseDto.builder()
                .reportId(report.getId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .status(report.getStatus())
                .reporter(convertToReportUserInfo(report.getReporter()))
                .createdAt(report.getCreateAt())
                .build();
    }

    private ReportDetailResponseDto convertToReportDetailResponse(Report report) {
        return ReportDetailResponseDto.builder()
                .reportId(report.getId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .status(report.getStatus())
                .reporter(convertToDetailUserInfo(report.getReporter()))
                .targetContent(getTargetInfo(report))
                .createdAt(report.getCreateAt())
                .build();
    }

    private ReportResponseDto.UserInfo convertToReportUserInfo(User user) {
        String profileImageBase64 = user.getProfileImage() != null ?
                Base64.getEncoder().encodeToString(user.getProfileImage()) : null;

        return ReportResponseDto.UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(profileImageBase64)
                .build();
    }

    private ReportDetailResponseDto.UserInfo convertToDetailUserInfo(User user) {
        String profileImageBase64 = user.getProfileImage() != null ?
                Base64.getEncoder().encodeToString(user.getProfileImage()) : null;

        return ReportDetailResponseDto.UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(profileImageBase64)
                .build();
    }

    private ReportDetailResponseDto.TargetInfo getTargetInfo(Report report) {
        return ReportDetailResponseDto.TargetInfo.builder()
                .content(getTargetContent(report))
                .contentUrl(getContentUrl(report))
                .writer(getTargetWriter(report))
                .build();
    }

    private String getTargetContent(Report report) {
        return switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findActiveById(report.getTargetId())
                    .map(Review::getReview)
                    .orElse("삭제된 리뷰입니다.");
            case COMMENT -> commentRepository.findActiveById(report.getTargetId())
                    .map(Comment::getComment)
                    .orElse("삭제된 댓글입니다.");
            case PHOTO_REVIEW -> photoReviewRepository.findActiveById(report.getTargetId())
                    .map(PhotoReview::getReview)
                    .orElse("삭제된 포토리뷰입니다.");
            case PHOTO_REVIEW_COMMENT -> photoReviewCommentRepository.findActiveById(report.getTargetId())
                    .map(PhotoReviewComment::getComment)
                    .orElse("삭제된 포토리뷰 댓글입니다.");
            case PHOTO_REVIEW_REPLY -> photoReviewReplyRepository.findActiveById(report.getTargetId())
                    .map(PhotoReviewReply::getReply)
                    .orElse("삭제된 포토리뷰 대댓글입니다.");
        };
    }

    private String getContentUrl(Report report) {
        return switch (report.getTargetType()) {
            case REVIEW -> String.format("/api/reviews/%d", report.getTargetId());
            case COMMENT -> {
                Comment comment = commentRepository.findActiveById(report.getTargetId())
                        .orElseThrow(() -> new CommentNotFoundException(report.getTargetId()));
                yield String.format("/api/reviews/%d", comment.getReview().getReviewId());
            }
            case PHOTO_REVIEW -> String.format("/api/photo-reviews/%d", report.getTargetId());
            case PHOTO_REVIEW_COMMENT -> {
                PhotoReviewComment comment = photoReviewCommentRepository.findActiveById(report.getTargetId())
                        .orElseThrow(() -> new PhotoReviewCommentNotFoundException(report.getTargetId()));
                yield String.format("/api/photo-reviews/%d", comment.getPhotoReview().getReviewId());
            }
            case PHOTO_REVIEW_REPLY -> {
                PhotoReviewReply reply = photoReviewReplyRepository.findActiveById(report.getTargetId())
                        .orElseThrow(() -> new PhotoReviewReplyNotFoundException(report.getTargetId()));
                yield String.format("/api/photo-reviews/%d", reply.getComment().getPhotoReview().getReviewId());
            }
        };
    }

    private ReportDetailResponseDto.UserInfo getTargetWriter(Report report) {
        User writer = switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findActiveById(report.getTargetId())
                    .map(Review::getUser)
                    .orElseThrow(() -> new ReviewNotFoundException(report.getTargetId()));
            case COMMENT -> commentRepository.findActiveById(report.getTargetId())
                    .map(Comment::getUser)
                    .orElseThrow(() -> new CommentNotFoundException(report.getTargetId()));
            case PHOTO_REVIEW -> photoReviewRepository.findActiveById(report.getTargetId())
                    .map(PhotoReview::getUser)
                    .orElseThrow(() -> new PhotoReviewNotFoundException(report.getTargetId()));
            case PHOTO_REVIEW_COMMENT -> photoReviewCommentRepository.findActiveById(report.getTargetId())
                    .map(PhotoReviewComment::getUser)
                    .orElseThrow(() -> new PhotoReviewCommentNotFoundException(report.getTargetId()));
            case PHOTO_REVIEW_REPLY -> photoReviewReplyRepository.findActiveById(report.getTargetId())
                    .map(PhotoReviewReply::getUser)
                    .orElseThrow(() -> new PhotoReviewReplyNotFoundException(report.getTargetId()));
        };

        String profileImageBase64 = writer.getProfileImage() != null ?
                Base64.getEncoder().encodeToString(writer.getProfileImage()) : null;

        return ReportDetailResponseDto.UserInfo.builder()
                .userId(writer.getUserId())
                .nickname(writer.getNickname())
                .profileImageUrl(profileImageBase64)
                .build();
    }

    private boolean isTargetHidden(Report report) {
        return switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findActiveById(report.getTargetId())
                    .map(Review::getIsHidden)
                    .orElse(true);
            case COMMENT -> commentRepository.findActiveById(report.getTargetId())
                    .map(Comment::getIsHidden)
                    .orElse(true);
            case PHOTO_REVIEW -> photoReviewRepository.findActiveById(report.getTargetId())
                    .map(PhotoReview::getIsHidden)
                    .orElse(true);
            case PHOTO_REVIEW_COMMENT -> photoReviewCommentRepository.findActiveById(report.getTargetId())
                    .map(PhotoReviewComment::getIsHidden)
                    .orElse(true);
            case PHOTO_REVIEW_REPLY -> photoReviewReplyRepository.findActiveById(report.getTargetId())
                    .map(PhotoReviewReply::getIsHidden)
                    .orElse(true);
        };
    }

    @Transactional
    protected void hideTargetContent(Report report) {
        switch (report.getTargetType()) {
            case REVIEW -> reviewRepository.findActiveById(report.getTargetId())
                    .ifPresent(Review::hide);
            case COMMENT -> commentRepository.findActiveById(report.getTargetId())
                    .ifPresent(Comment::hide);
            case PHOTO_REVIEW -> photoReviewRepository.findActiveById(report.getTargetId())
                    .ifPresent(PhotoReview::hide);
            case PHOTO_REVIEW_COMMENT -> photoReviewCommentRepository.findActiveById(report.getTargetId())
                    .ifPresent(PhotoReviewComment::hide);
            case PHOTO_REVIEW_REPLY -> photoReviewReplyRepository.findActiveById(report.getTargetId())
                    .ifPresent(PhotoReviewReply::hide);
        }
    }
}
