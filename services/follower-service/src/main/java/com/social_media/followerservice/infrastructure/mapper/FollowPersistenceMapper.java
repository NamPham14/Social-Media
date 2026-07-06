package com.social_media.followerservice.infrastructure.mapper;

import com.social_media.followerservice.domain.model.follow.aggregate.FollowRelation;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import com.social_media.followerservice.infrastructure.persistence.entity.FollowRelationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface FollowPersistenceMapper {

    @Mapping(target = "followerId", source = "followerId.value")
    @Mapping(target = "followingId", source = "followingId.value")
    @Mapping(target = "createdAt", ignore = true)
    FollowRelationEntity toEntity(FollowRelation domain);

    @Mapping(target = "followerId", source = "followerId")
    @Mapping(target = "followingId", source = "followingId")
    @Mapping(target = "createdAt", source = "createdAt")
    FollowRelation toDomain(FollowRelationEntity entity);

    default UserId map(Long value) { return value != null ? UserId.from(value) : null; }
    default LocalDateTime map(Instant value) { return value != null ? value.atZone(ZoneId.systemDefault()).toLocalDateTime() : null; }
}
