package com.social_media.postservice.application.usecase.post;


import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.application.exception.ResourceNotFoundException;
import com.social_media.postservice.config.security.SecurityUtils;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.model.post.valueobject.ModerationStatus;
import com.social_media.postservice.domain.repository.PostRepository;
import com.social_media.postservice.infrastructure.client.comment.service.CommentServiceHelper;
import com.social_media.postservice.infrastructure.client.follower.service.FollowerServiceHelper;
import com.social_media.postservice.infrastructure.client.interaction.service.InteractionServiceHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetPostByPostIdUseCase {

    PostRepository postRepository;
    FollowerServiceHelper followerServiceHelper;
    InteractionServiceHelper interactionServiceHelper;
    CommentServiceHelper commentServiceHelper;

    public PostResponse execute(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException());

        UUID viewerId = SecurityUtils.getCurrentUserId();
        List<UUID> followingIds = followerServiceHelper.getFollowingIds(viewerId);

        if (!canView(post, viewerId, followingIds)) {
            throw new ResourceNotFoundException();
        }

        PostResponse response = PostResponse.from(post);

        Map<UUID, Integer> likeCounts = interactionServiceHelper.getLikeCounts(List.of(postId));
        Map<UUID, Integer> commentCounts = commentServiceHelper.getCommentCounts(List.of(postId));
        response.setLikeCount(likeCounts.getOrDefault(postId, 0));
        response.setCommentCount(commentCounts.getOrDefault(postId, 0));

        return response;
    }

    private boolean canView(Post post, UUID viewerId, List<UUID> followingIds) {
        if (post.getModerationStatus() == ModerationStatus.REMOVED) {
            return false;
        }

        return switch (post.getStatus()) {
            case PUBLIC -> true;
            case PRIVATE -> post.getUserId().equals(viewerId);
            case FRIENDS -> post.getUserId().equals(viewerId) || followingIds.contains(post.getUserId());
        };
    }

}
