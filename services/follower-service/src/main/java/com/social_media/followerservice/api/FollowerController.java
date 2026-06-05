package com.social_media.followerservice.api;

import com.social_media.followerservice.api.dto.FeedResponse;
import com.social_media.followerservice.application.usecase.GetFollowersUseCase;
import com.social_media.followerservice.application.usecase.GetFollowingUseCase;
import com.social_media.followerservice.application.usecase.GetNewsFeedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
public class FollowerController {

    private final GetFollowersUseCase getFollowersUseCase;
    private final GetFollowingUseCase getFollowingUseCase;
    private final GetNewsFeedUseCase getNewsFeedUseCase;


    @GetMapping(ApiPath.FOLLOWER_API)
    public ResponseEntity<List<Long>> getFollowers(@PathVariable("id") Long id) {
        return ResponseEntity.ok(getFollowersUseCase.getFollowers(id));
    }

    @GetMapping(ApiPath.FOLLOWING_API)
    public ResponseEntity<List<Long>> getFollowing(@PathVariable("id") Long id) {
        return ResponseEntity.ok(getFollowingUseCase.getFollowing(id));
    }

    @GetMapping(ApiPath.FEED_API)
    public ResponseEntity<List<FeedResponse>> getFeeds(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
            /* e.g., @RequestHeader("X-User-Id") Long currentUserId */) {
        // Hardcoded or extracted user ID would go here, using a dummy 1L for the stub
        Long currentUserId = 1L; 
        return ResponseEntity.ok(getNewsFeedUseCase.getNewsFeed(currentUserId, page, size));
    }
}

