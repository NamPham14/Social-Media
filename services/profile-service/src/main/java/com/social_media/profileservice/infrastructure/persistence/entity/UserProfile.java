package com.social_media.profileservice.infrastructure.persistence.entity;

import com.social_media.common.base.BaseEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends BaseEntity {
    private UUID id;
    private String username;
    private String fullName;
    private String bio;
    private String avatarUrl;


}
