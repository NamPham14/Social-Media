package com.social_media.profileservice.infrastructure;

import com.social_media.profileservice.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserProfileJpaRepository extends JpaRepository<UserProfile, UUID> {
}
