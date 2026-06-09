package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggregate.Bookmark;
import com.social_media.postservice.infrastructure.entity.BookmarkEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookmarkMapper {

    BookmarkEntity toEntity(Bookmark domain);

    Bookmark toDomain(BookmarkEntity entity);
}
