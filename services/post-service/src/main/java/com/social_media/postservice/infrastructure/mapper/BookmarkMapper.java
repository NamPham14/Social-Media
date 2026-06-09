package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.Bookmark;
import com.social_media.postservice.infrastructure.entity.BookmarkEntity;

public interface BookmarkMapper {

    BookmarkEntity toEntity(Bookmark domain);

    Bookmark toDomain(BookmarkEntity entity);
}
