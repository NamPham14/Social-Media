package com.social_media.profileservice.infrastructure.persistence.repository;

import com.social_media.profileservice.infrastructure.persistence.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserProfileJpaRepository extends JpaRepository<UserProfile, UUID> {
    @Query("SELECT p FROM UserProfile p WHERE " +
            "(:keyword IS NULL OR LOWER(p.username) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<UserProfile> searchProfiles(@Param("keyword") String keyword, Pageable pageable);
}
