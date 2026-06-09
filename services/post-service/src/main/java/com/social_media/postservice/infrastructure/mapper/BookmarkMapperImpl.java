package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.Bookmark;
import com.social_media.postservice.infrastructure.entity.BookmarkEntity;
import org.springframework.stereotype.Component;

@Component
public class BookmarkMapperImpl implements BookmarkMapper {

    @Override
    public BookmarkEntity toEntity(Bookmark domain) {
        if (domain == null) return null;

        return BookmarkEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .postId(domain.getPostId())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    @Override
    public Bookmark toDomain(BookmarkEntity entity) {
        if (entity == null) return null;

        return Bookmark.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .postId(entity.getPostId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
