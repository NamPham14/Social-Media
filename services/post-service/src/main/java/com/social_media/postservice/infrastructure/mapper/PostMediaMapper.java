package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.PostMedia;
import com.social_media.postservice.infrastructure.entity.PostMediaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMediaMapper {

    PostMediaEntity toEntity(PostMedia domain);

    PostMedia toDomain(PostMediaEntity entity);
}
