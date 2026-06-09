package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.PostMedia;
import com.social_media.postservice.infrastructure.entity.PostMediaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMediaMapperImpl implements PostMediaMapper {

    @Override
    public PostMediaEntity toEntity(PostMedia domain) {
        if (domain == null) return null;

        return PostMediaEntity.builder()
                .id(domain.getId())
                .mediaUrl(domain.getMediaUrl())
                .publicId(domain.getPublicId())
                .mediaType(domain.getMediaType())
                .orderIndex(domain.getOrderIndex())
                .build();
    }

    @Override
    public PostMedia toDomain(PostMediaEntity entity) {
        if (entity == null) return null;

        return PostMedia.builder()
                .id(entity.getId())
                .mediaUrl(entity.getMediaUrl())
                .publicId(entity.getPublicId())
                .mediaType(entity.getMediaType())
                .orderIndex(entity.getOrderIndex())
                .build();
    }
}
