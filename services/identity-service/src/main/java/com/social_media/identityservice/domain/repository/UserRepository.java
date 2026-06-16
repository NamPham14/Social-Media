package com.social_media.identityservice.domain.repository;

import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.shared.valueobject.UserId;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
