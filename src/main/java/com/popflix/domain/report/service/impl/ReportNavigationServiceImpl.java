package com.popflix.domain.report.service.impl;

import com.popflix.domain.photoreview.entity.PhotoReview;
import com.popflix.domain.photoreview.entity.PhotoReviewComment;
import com.popflix.domain.photoreview.entity.PhotoReviewReply;
import com.popflix.domain.photoreview.exception.PhotoReviewCommentNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewReplyNotFoundException;
import com.popflix.domain.photoreview.repository.PhotoReviewCommentRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewReplyRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewRepository;
import com.popflix.domain.report.dto.ReportNavigationDto;
import com.popflix.domain.report.entity.Report;
import com.popflix.domain.report.enums.ReportTarget;
import com.popflix.domain.report.exception.ReportNotFoundException;
import com.popflix.domain.report.repository.ReportRepository;
import com.popflix.domain.report.service.ReportNavigationService;
import com.popflix.domain.review.entity.Comment;
import com.popflix.domain.review.entity.Review;
import com.popflix.domain.review.exception.CommentNotFoundException;
import com.popflix.domain.review.exception.ReviewNotFoundException;
import com.popflix.domain.review.repository.CommentRepository;
import com.popflix.domain.review.repository.ReviewRepository;
import com.popflix.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportNavigationServiceImpl implements ReportNavigationService {

    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final PhotoReviewRepository photoReviewRepository;
    private final PhotoReviewCommentRepository photoReviewCommentRepository;
    private final PhotoReviewReplyRepository photoReviewReplyRepository;

    @Override
    public ReportNavigationDto getNavigationInfo(Long reportId) {
        Report report = reportRepository.findActiveById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        User author = getTargetAuthor(report);
        Long movieId = getMovieId(report);

        return ReportNavigationDto.builder()
                .targetId(report.getTargetId())
                .movieId(movieId)
                .navigationUrl(generateNavigationUrl(report))
                .content(getTargetContent(report))
                .authorId(author.getUserId())
                .authorName(author.getNickname())
                .build();
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
}