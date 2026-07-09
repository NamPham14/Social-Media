package com.social_media.identityservice.infrastructure.mapper;

import com.social_media.identityservice.domain.model.role.aggregate.Role;
import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import com.social_media.identityservice.infrastructure.persistence.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RolePersistenceMapper {

    public Role toDomain(RoleEntity entity) {
        if (entity == null) return null;

        return Role.reconstruct(
                RoleId.from(entity.getName()),
                entity.getDescription()
        );
    }

    public RoleEntity toEntity(Role domain) {
        if (domain == null) return null;

        return RoleEntity.builder()
                .name(domain.getId().value())
                .description(domain.getDescription())
                .build();
    }
}
