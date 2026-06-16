package com.social_media.profileservice.infrastructure.persistence.repository;

import com.social_media.profileservice.infrastructure.persistence.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserProfileJpaRepository extends JpaRepository<UserProfile, UUID> {
}
