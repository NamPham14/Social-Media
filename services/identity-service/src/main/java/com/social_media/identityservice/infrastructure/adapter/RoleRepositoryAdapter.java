package com.social_media.identityservice.infrastructure.adapter;

import com.social_media.identityservice.domain.model.role.aggregate.Role;
import com.social_media.identityservice.domain.repository.RoleRepository;
import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import com.social_media.identityservice.infrastructure.mapper.RolePersistenceMapper;
import com.social_media.identityservice.infrastructure.persistence.entity.RoleEntity;
import com.social_media.identityservice.infrastructure.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    @Override
    public Role save(Role role) {
        RoleEntity entity = rolePersistenceMapper.toEntity(role);
        RoleEntity savedEntity = roleJpaRepository.save(entity);
        return rolePersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        return roleJpaRepository.findById(id.value())
                .map(rolePersistenceMapper::toDomain);
    }
}
