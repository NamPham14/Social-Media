package com.social_media.profileservice.infrastructure.mapper;


import com.social_media.profileservice.domain.model.aggregate.Profile;
import com.social_media.profileservice.domain.shared.valueobject.UserProfileId;
import com.social_media.profileservice.infrastructure.persistence.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProfilePersistenceMapper {


    //map domain -> entity (luu database)
    @Mapping(target = "id" , source = "id.value")
    UserProfile toEntity(Profile domain);

    // Map Entity -> Domain (Để lấy dữ liệu ra xử lý)
    @Mapping(target = "id" , source = "id")
    Profile toDomain(UserProfile entity);

    // Hàm hỗ trợ chuyển đổi từ UUID (DB) sang UserProfileId (Domain)
    default UserProfileId map(UUID value) {
        return value != null ? UserProfileId.from(value) : null;
    }





    }
