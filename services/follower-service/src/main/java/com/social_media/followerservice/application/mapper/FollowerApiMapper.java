package com.social_media.followerservice.application.mapper;

import com.social_media.followerservice.api.dto.FollowResponse;
import com.social_media.followerservice.domain.model.follow.aggregate.FollowRelation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowerApiMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "followerId", source = "followerId.value")
    @Mapping(target = "followingId", source = "followingId.value")
    @Mapping(target = "followedAt", source = "createdAt")
    FollowResponse toResponse(FollowRelation domain);
}
