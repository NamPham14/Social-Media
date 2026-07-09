package com.social_media.identityservice.domain.repository;

import com.social_media.identityservice.domain.model.role.aggregate.Role;
import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(RoleId id);
}
