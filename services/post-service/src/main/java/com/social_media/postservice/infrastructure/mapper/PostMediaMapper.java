package com.social_media.postservice.infrastructure.mapper;

//import com.social_media.postservice.domain.model.post.valueobject.PostMedia;
import com.social_media.postservice.domain.model.post.valueobject.PostMedia;
import com.social_media.postservice.infrastructure.entity.PostMediaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMediaMapper {

    @Mapping(target = "post", ignore = true)
    PostMediaEntity toEntity(PostMedia domain);

    PostMedia toDomain(PostMediaEntity entity);
}

