package com.social_media.profileservice.infrastructure;

import com.social_media.profileservice.domain.ProfileRepository;
import com.social_media.profileservice.domain.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryAdapter implements ProfileRepository {
    private final UserProfileJpaRepository jpaRepository;

    @Override
    public UserProfile save(UserProfile profile) {
        return jpaRepository.save(profile);
    }

    @Override
    public Optional<UserProfile> findById(UUID id) {
        return jpaRepository.findById(id);
    }
}
