package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentCountResponse;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCommentCountsUseCaseImpl implements GetCommentCountsUseCase {

    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public CommentCountResponse get(UUID postId) {
        return new CommentCountResponse(postId, commentRepository.countActiveByPostId(postId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentCountResponse> getBatch(List<UUID> postIds) {
        List<UUID> distinctPostIds = postIds.stream().distinct().toList();
        Map<UUID, Long> counts = commentRepository.countActiveByPostIds(distinctPostIds);
        return distinctPostIds.stream()
                .map(postId -> new CommentCountResponse(postId, counts.getOrDefault(postId, 0L)))
                .toList();
    }
}
