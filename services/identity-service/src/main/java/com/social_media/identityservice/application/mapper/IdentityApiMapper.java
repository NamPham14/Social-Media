package com.social_media.identityservice.application.mapper;

import com.social_media.identityservice.api.dto.response.UserResponse;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IdentityApiMapper {
    @Mapping(target = "id", source = "id.value")
    UserResponse toResponse(User domain);

    default String map(RoleId roleId) {
        return roleId != null ? roleId.value() : null;
    }
}
