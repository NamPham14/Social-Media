package com.social_media.followerservice.application.usecase.impl;

import com.social_media.common.api.ApiResponse;
import com.social_media.followerservice.api.dto.FeedResponse;
import com.social_media.followerservice.application.usecase.GetNewsFeedUseCase;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import com.social_media.followerservice.infrastructure.client.ArticleClient;
import com.social_media.followerservice.infrastructure.client.ProfileServiceClient;
import com.social_media.followerservice.infrastructure.client.dto.PostServicePostResponse;
import com.social_media.followerservice.infrastructure.client.identity.ProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetNewsFeedUseCaseImpl implements GetNewsFeedUseCase {

    private final FollowRelationRepository followRelationRepository;
    private final ArticleClient articleClient;
    private final ProfileServiceClient profileServiceClient;

    @Override
    public List<FeedResponse> execute(UUID currentUserId, int page, int size) {
        List<UserId> followingIds = followRelationRepository.findFollowingIdsByFollowerId(UserId.from(currentUserId));

        if (followingIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> followingUuids = followingIds.stream()
                .map(UserId::value)
                .collect(Collectors.toList());

        ApiResponse<List<PostServicePostResponse>> response;
        try {
            response = articleClient.getLatestPostsByAuthorIds(followingUuids, page - 1, size);
        } catch (Exception e) {
            log.error("Failed to fetch posts from post-service: {}", e.getMessage());
            return Collections.emptyList();
        }

        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return Collections.emptyList();
        }

        Map<UUID, ProfileResponse> profileMap = new HashMap<>();
        for (PostServicePostResponse post : response.getData()) {
            if (!profileMap.containsKey(post.getUserId())) {
                try {
                    ApiResponse<ProfileResponse> profileApiResp = profileServiceClient.getProfile(post.getUserId());
                    if (profileApiResp != null && profileApiResp.getData() != null) {
                        profileMap.put(post.getUserId(), profileApiResp.getData());
                    }
                } catch (Exception e) {
                    log.warn("Failed to get profile for user {}: {}", post.getUserId(), e.getMessage());
                }
            }
        }

        List<FeedResponse> feeds = new ArrayList<>();
        for (PostServicePostResponse post : response.getData()) {
            ProfileResponse profile = profileMap.get(post.getUserId());
            String imageUrl = null;
            if (post.getMedias() != null && !post.getMedias().isEmpty()) {
                imageUrl = post.getMedias().get(0).getUrl();
            }

            feeds.add(FeedResponse.builder()
                    .postId(post.getId())
                    .content(post.getCaption())
                    .authorId(post.getUserId())
                    .authorName(profile != null ? profile.getFullName() : post.getUserId().toString())
                    .authorAvatarUrl(profile != null ? profile.getAvatarUrl() : null)
                    .createdAt(post.getCreatedAt() != null
                            ? post.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                            : null)
                    .build());
        }

        return feeds;
    }
}
