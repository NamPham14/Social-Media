package com.social_media.profileservice.infrastructure.adapter;

import com.social_media.profileservice.domain.model.aggregate.Profile;
import com.social_media.profileservice.domain.repository.ProfileRepository;
import com.social_media.profileservice.infrastructure.mapper.ProfilePersistenceMapper;
import com.social_media.profileservice.infrastructure.persistence.entity.UserProfile;
import com.social_media.profileservice.infrastructure.persistence.repository.UserProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryAdapter implements ProfileRepository {
    private final UserProfileJpaRepository jpaRepository;
    private final ProfilePersistenceMapper mapper;

    @Override
    public Profile save(Profile profile) {
        UserProfile entity = mapper.toEntity(profile);
        UserProfile result = jpaRepository.save(entity);
        return mapper.toDomain(result);
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
