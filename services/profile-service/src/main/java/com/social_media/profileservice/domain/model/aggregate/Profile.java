package com.social_media.profileservice.domain.model.aggregate;

import com.social_media.profileservice.domain.shared.valueobject.UserProfileId;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private UserProfileId id;

    private String username;
    private String fullName;
    private String bio;
    private String avatarUrl;


    public static Profile createNewProfile(UserProfileId id, String username, String fullName) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }


        return Profile.builder()
                .id(id)
                .username(username)
                .fullName(fullName)
                .build();
    }

    public void updateInfo(String fullName, String bio, String avatarUrl) {
        this.fullName = fullName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }
}
