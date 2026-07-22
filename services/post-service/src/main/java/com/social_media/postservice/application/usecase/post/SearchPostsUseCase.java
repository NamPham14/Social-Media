package com.social_media.postservice.application.usecase.post;

import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.application.exception.ResourceNotFoundException;
import com.social_media.postservice.config.security.SecurityUtils;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import com.social_media.postservice.infrastructure.client.comment.service.CommentServiceHelper;
import com.social_media.postservice.infrastructure.client.follower.service.FollowerServiceHelper;
import com.social_media.postservice.infrastructure.client.interaction.service.InteractionServiceHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchPostsUseCase {

    PostRepository postRepository;
    FollowerServiceHelper followerServiceHelper;
    InteractionServiceHelper interactionServiceHelper;
    CommentServiceHelper commentServiceHelper;

    public Page<PostResponse> execute(String keyword, Pageable pageable) {
        UUID viewerId = SecurityUtils.getCurrentUserId();
        List<UUID> followingIds = followerServiceHelper.getFollowingIds(viewerId);

        Page<Post> page = postRepository.searchByKeyword("%" + keyword + "%", pageable, viewerId, followingIds);

        if (page.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        Page<PostResponse> responsePage = page.map(PostResponse::from);

        List<UUID> postIds = responsePage.getContent().stream().map(PostResponse::getId).toList();
        if (!postIds.isEmpty()) {
            Map<UUID, Integer> likeCounts = interactionServiceHelper.getLikeCounts(postIds);
            Map<UUID, Integer> commentCounts = commentServiceHelper.getCommentCounts(postIds);
            // Hiếu thêm
            Map<UUID, Boolean> likedByMe = interactionServiceHelper.getLikedByMe(postIds);

            for (PostResponse post : responsePage.getContent()) {
                int likeCount = likeCounts.getOrDefault(post.getId(), 0);
                int commentCount = commentCounts.getOrDefault(post.getId(), 0);

                post.setLikeCount(likeCount);
                post.setCommentCount(commentCount);
                post.setLiked(likedByMe.getOrDefault(post.getId(), false));

            }
        }

        return responsePage;
    }
}
