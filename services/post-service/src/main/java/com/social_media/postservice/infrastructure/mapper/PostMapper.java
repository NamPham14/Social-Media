package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.Post;
import com.social_media.postservice.infrastructure.entity.PostEntity;

public interface PostMapper {

    PostEntity toEntity(Post domain);

    Post toDomain(PostEntity entity);
}
