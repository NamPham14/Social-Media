package com.social_media.identityservice.domain.model.role.aggregate;

import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import lombok.*;

@Getter
public class Role {
    private RoleId id;
    private String description;

    private Role() {}

    public static Role reconstruct(RoleId id, String description) {
        Role role = new Role();
        role.id = id;
        role.description = description;
        return role;
    }

    public static Role create(String name, String description) {
        Role role = new Role();
        role.id = RoleId.from(name);
        role.description = description;
        return role;
    }
}
