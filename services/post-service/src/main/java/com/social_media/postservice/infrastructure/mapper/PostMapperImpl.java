package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.Post;
import com.social_media.postservice.infrastructure.entity.PostEntity;
import com.social_media.postservice.infrastructure.entity.PostMediaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapperImpl implements PostMapper {

    private final PostMediaMapper mediaMapper;

    @Override
    public PostEntity toEntity(Post domain) {
        if (domain == null) return null;

        PostEntity entity = PostEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .caption(domain.getCaption())
                .locationName(domain.getLocationName())
                .status(domain.getStatus())
                .moderationStatus(domain.getModerationStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deleted(domain.isDeleted())
                .deletedAt(domain.getDeletedAt())
                .removedBy(domain.getRemovedBy())
                .removedAt(domain.getRemovedAt())
                .build();

        if (domain.getMedias() != null) {
            List<PostMediaEntity> mediaEntities = domain.getMedias().stream()
                    .map(mediaMapper::toEntity)
                    .collect(Collectors.toList());
            entity.setMedias(mediaEntities);
            mediaEntities.forEach(me -> me.setPost(entity));
        }

        return entity;
    }

    @Override
    public Post toDomain(PostEntity entity) {
        if (entity == null) return null;

        return Post.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .caption(entity.getCaption())
                .locationName(entity.getLocationName())
                .status(entity.getStatus())
                .moderationStatus(entity.getModerationStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deleted(entity.isDeleted())
                .deletedAt(entity.getDeletedAt())
                .removedBy(entity.getRemovedBy())
                .removedAt(entity.getRemovedAt())
                .medias(entity.getMedias() != null
                        ? entity.getMedias().stream().map(mediaMapper::toDomain).collect(Collectors.toList())
                        : null)
                .build();
    }
}
