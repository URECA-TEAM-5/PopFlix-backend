package com.popflix.domain.photoreview.service.impl;

import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.photoreview.dto.PhotoReviewReplyPatchDto;
import com.popflix.domain.photoreview.dto.PhotoReviewReplyPostDto;
import com.popflix.domain.photoreview.dto.PhotoReviewReplyResponseDto;
import com.popflix.domain.photoreview.entity.PhotoReviewComment;
import com.popflix.domain.photoreview.entity.PhotoReviewReply;
import com.popflix.domain.photoreview.entity.PhotoReviewReplyLike;
import com.popflix.domain.photoreview.exception.PhotoReviewCommentNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewReplyLikeNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewReplyNotFoundException;
import com.popflix.domain.photoreview.exception.UnauthorizedPhotoReviewAccessException;
import com.popflix.domain.photoreview.repository.PhotoReviewCommentRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewReplyLikeRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewReplyRepository;
import com.popflix.domain.photoreview.service.PhotoReviewReplyService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PhotoReviewReplyServiceImpl implements PhotoReviewReplyService {
    private final PhotoReviewReplyRepository replyRepository;
    private final PhotoReviewCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PhotoReviewReplyLikeRepository replyLikeRepository;

    @Override
    @Transactional
    public PhotoReviewReplyResponseDto createReply(PhotoReviewReplyPostDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        PhotoReviewComment comment = commentRepository.findActiveById(requestDto.getCommentId())
                .orElseThrow(() -> new PhotoReviewCommentNotFoundException(requestDto.getCommentId()));

        PhotoReviewReply reply = PhotoReviewReply.builder()
                .reply(requestDto.getReply())
                .comment(comment)
                .user(user)
                .build();

        PhotoReviewReply savedReply = replyRepository.save(reply);
        return convertToReplyResponse(savedReply);
    }

    @Override
    @Transactional
    public PhotoReviewReplyResponseDto updateReply(Long replyId, PhotoReviewReplyPatchDto requestDto) {
        PhotoReviewReply reply = findReplyById(replyId);
        reply.updateReply(requestDto.getReply());
        return convertToReplyResponse(reply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId, Long userId) {
        PhotoReviewReply reply = findReplyById(replyId);
        validateReplyOwner(reply, userId);
        reply.delete();
    }

    @Override
    public List<PhotoReviewReplyResponseDto> getRepliesByCommentId(Long commentId) {
        List<PhotoReviewReply> replies = replyRepository.findAllByCommentId(commentId);
        return replies.stream()
                .map(this::convertToReplyResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void likeReply(Long replyId, Long userId) {
        PhotoReviewReply reply = findReplyById(replyId);
        validateUser(userId);

        PhotoReviewReplyLike replyLike = getOrCreateReplyLike(reply, userId);
        saveOrToggleReplyLike(replyLike);
    }

    @Override
    @Transactional
    public void unlikeReply(Long replyId, Long userId) {
        PhotoReviewReplyLike replyLike = findReplyLike(replyId, userId);
        replyLike.toggleLike();
    }

    // Private helper methods
    private PhotoReviewReply findReplyById(Long replyId) {
        return replyRepository.findActiveById(replyId)
                .orElseThrow(() -> new PhotoReviewReplyNotFoundException(replyId));
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateReplyOwner(PhotoReviewReply reply, Long userId) {
        if (!reply.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedPhotoReviewAccessException("대댓글을 삭제할 권한이 없습니다.");
        }
    }

    private PhotoReviewReplyLike getOrCreateReplyLike(PhotoReviewReply reply, Long userId) {
        return replyLikeRepository.findActiveByReplyIdAndUserId(reply.getReplyId(), userId)
                .orElseGet(() -> PhotoReviewReplyLike.builder()
                        .reply(reply)
                        .user(userRepository.getReferenceById(userId))
                        .build());
    }

    private void saveOrToggleReplyLike(PhotoReviewReplyLike replyLike) {
        if (replyLike.getReplyLikeId() == null) {
            replyLikeRepository.save(replyLike);
        } else if (!replyLike.getReplyLike()) {
            replyLike.toggleLike();
        }
    }

    private PhotoReviewReplyLike findReplyLike(Long replyId, Long userId) {
        return replyLikeRepository.findActiveByReplyIdAndUserId(replyId, userId)
                .orElseThrow(() -> new PhotoReviewReplyLikeNotFoundException(replyId, userId));
    }

    private PhotoReviewReplyResponseDto convertToReplyResponse(PhotoReviewReply reply) {
        return PhotoReviewReplyResponseDto.builder()
                .replyId(reply.getReplyId())
                .reply(reply.getReply())
                .user(convertToReplyUserInfo(reply.getUser()))
                .createdAt(reply.getCreateAt())
                .likeCount(replyLikeRepository.countByReplyId(reply.getReplyId()))
                .isLiked(false)
                .isHidden(reply.getIsHidden())
                .build();
    }

    private PhotoReviewReplyResponseDto.UserInfo convertToReplyUserInfo(User user) {
        String profileImageBase64 = user.getProfileImage() != null ?
                Base64.getEncoder().encodeToString(user.getProfileImage()) : null;

        return PhotoReviewReplyResponseDto.UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(profileImageBase64)
                .build();
    }
}
