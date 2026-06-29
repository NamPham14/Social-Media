package com.social_media.followerservice.api;

import com.social_media.followerservice.application.command.FollowUserCommand;
import com.social_media.followerservice.application.command.UnfollowUserCommand;
import com.social_media.followerservice.application.usecase.FollowUserUseCase;
import com.social_media.followerservice.application.usecase.GetFollowersUseCase;
import com.social_media.followerservice.application.usecase.GetFollowingUseCase;
import com.social_media.followerservice.application.usecase.UnfollowUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowerController {

    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final GetFollowersUseCase getFollowersUseCase;
    private final GetFollowingUseCase getFollowingUseCase;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<Void> followUser(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @PathVariable("targetUserId") UUID targetUserId) {
        
        followUserUseCase.followUser(new FollowUserCommand(currentUserId, targetUserId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> unfollowUser(@RequestHeader("X-User-Id") UUID currentUserId,
            @PathVariable("targetUserId") UUID targetUserId) {
        
        unfollowUserUseCase.unfollowUser(new UnfollowUserCommand(currentUserId, targetUserId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<UUID>> getFollowers(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(getFollowersUseCase.getFollowers(userId));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<UUID>> getFollowing(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(getFollowingUseCase.getFollowing(userId));
    }
}

