package com.social_media.followerservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.common.api.PageResponse;
import com.social_media.followerservice.api.dto.FeedResponse;
import com.social_media.followerservice.api.dto.FollowRequest;
import com.social_media.followerservice.api.dto.FollowResponse;
import com.social_media.followerservice.api.path.ApiPath;
import com.social_media.followerservice.application.command.FollowUserCommand;
import com.social_media.followerservice.application.command.UnfollowUserCommand;
import com.social_media.followerservice.application.usecase.*;
import com.social_media.followerservice.config.security.SecurityUtils;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class FollowerController {

    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final GetFollowersUseCase getFollowersUseCase;
    private final GetFollowingUseCase getFollowingUseCase;
    private final GetNewsFeedUseCase getNewsFeedUseCase;

    @PostMapping(ApiPath.INTERNAL + "/follow")
    public ResponseEntity<ApiResponse<Void>> followUser(@RequestBody FollowRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        FollowUserCommand cmd = new FollowUserCommand(UserId.from(currentUserId), UserId.from(request.getFollowingId()));
        followUserUseCase.execute(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Void>success(null, "Follow successfully"));
    }

    @PostMapping(ApiPath.INTERNAL + "/unfollow")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(@RequestBody FollowRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UnfollowUserCommand cmd = new UnfollowUserCommand(UserId.from(currentUserId), UserId.from(request.getFollowingId()));
        unfollowUserUseCase.execute(cmd);
        return ResponseEntity.ok(ApiResponse.<Void>success(null, "Unfollow successfully"));
    }

    @GetMapping("/users/{id}/followers")
    public ResponseEntity<ApiResponse<PageResponse<FollowResponse>>> getFollowers(
            @PathVariable("id") UUID id,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<FollowResponse> result = getFollowersUseCase.execute(id, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(result), "Get followers successfully"));
    }

    @GetMapping("/users/{id}/following")
    public ResponseEntity<ApiResponse<PageResponse<FollowResponse>>> getFollowing(
            @PathVariable("id") UUID id,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<FollowResponse> result = getFollowingUseCase.execute(id, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(result), "Get following successfully"));
    }

    @GetMapping("/feeds")
    public ResponseEntity<ApiResponse<List<FeedResponse>>> getFeeds(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        List<FeedResponse> feeds = getNewsFeedUseCase.execute(currentUserId, page, size);
        return ResponseEntity.ok(ApiResponse.success(feeds, "Get news feed successfully"));
    }
}
