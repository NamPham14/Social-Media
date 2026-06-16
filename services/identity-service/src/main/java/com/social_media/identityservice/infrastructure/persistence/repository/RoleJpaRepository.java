package com.social_media.identityservice.infrastructure.persistence.repository;

import com.social_media.identityservice.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, String> {
}
