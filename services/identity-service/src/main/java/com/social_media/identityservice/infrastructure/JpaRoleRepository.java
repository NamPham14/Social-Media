package com.social_media.identityservice.infrastructure;

import com.social_media.identityservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
