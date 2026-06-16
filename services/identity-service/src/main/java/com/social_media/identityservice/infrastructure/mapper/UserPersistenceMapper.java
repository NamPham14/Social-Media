package com.social_media.identityservice.infrastructure.mapper;

import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import com.social_media.identityservice.infrastructure.persistence.entity.RoleEntity;
import com.social_media.identityservice.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return User.reconstruct(
                UserId.from(entity.getId()),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getRoles().stream()
                        .map(role -> RoleId.from(role.getName()))
                        .collect(Collectors.toSet())
        );
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        return UserEntity.builder()
                .id(domain.getId().value())
                .username(domain.getUsername())
                .password(domain.getPassword())
                .email(domain.getEmail())
                .roles(domain.getRoles().stream()
                        .map(roleId -> RoleEntity.builder().name(roleId.value()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
