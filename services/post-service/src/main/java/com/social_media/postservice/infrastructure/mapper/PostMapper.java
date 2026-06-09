package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggregate.Post;
import com.social_media.postservice.infrastructure.entity.PostEntity;
import com.social_media.postservice.infrastructure.entity.PostMediaEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = PostMediaMapper.class)
public interface PostMapper {

    PostEntity toEntity(Post domain);

    Post toDomain(PostEntity entity);

    @AfterMapping
    default void linkMedias(@MappingTarget PostEntity entity) {
        if (entity.getMedias() != null) {
            entity.getMedias().forEach(me -> me.setPost(entity));
        }
    }
}
