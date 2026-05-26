package com.social_media.profileservice.domain;

import com.social_media.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user_profile")
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;
    private String fullName;
    private String bio;
    private String avatarUrl;

    public static UserProfile createNewProfile(UUID id, String username, String fullName) {
        if (id == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        UserProfile profile = new UserProfile();
        profile.id = id;
        profile.username = username;
        profile.fullName = fullName;
        return profile;
    }

    public void updateInfo(String fullName, String bio, String avatarUrl) {
        this.fullName = fullName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }
}
