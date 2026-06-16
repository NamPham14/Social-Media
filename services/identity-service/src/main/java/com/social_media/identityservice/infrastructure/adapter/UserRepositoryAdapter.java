package com.social_media.identityservice.infrastructure.adapter;

import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import com.social_media.identityservice.infrastructure.mapper.UserPersistenceMapper;
import com.social_media.identityservice.infrastructure.persistence.entity.UserEntity;
import com.social_media.identityservice.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public User save(User user) {
        UserEntity entity = userPersistenceMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value())
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
