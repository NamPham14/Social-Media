package com.social_media.profileservice.application.mapper;


import com.social_media.profileservice.api.dto.ProfileResponse;
import com.social_media.profileservice.domain.model.aggregate.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileApiMapper {
    // Map từ Domain Aggregate sang Response DTO
    @Mapping(target = "id", source = "id.value")
    ProfileResponse toResponse(Profile domain);
}
