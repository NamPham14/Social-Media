package com.social_media.profileservice.infrastructure.persistence.entity;

import com.social_media.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profile")
public class UserProfile extends BaseEntity {
    @Id
    private UUID id;
    private String username;
    private String fullName;
    private String bio;
    private String avatarUrl;


}
