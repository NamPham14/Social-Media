package com.social_media.followerservice.application.usecase;
import com.social_media.followerservice.application.command.FollowUserCommand;
import com.social_media.followerservice.domain.model.follow.aggregate.FollowRelation;

public interface FollowUserUseCase {
    FollowRelation execute(FollowUserCommand command);
}
