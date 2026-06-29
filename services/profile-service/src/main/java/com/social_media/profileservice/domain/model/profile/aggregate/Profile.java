package com.social_media.profileservice.domain.model.profile.aggregate;

import com.social_media.profileservice.domain.shared.valueobject.UserProfileId;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private UserProfileId id;
    private String username;
    private String fullName;
    private String bio;
    private String avatarUrl;

    public static Profile create(UserProfileId id, String username, String fullName) {
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
    public void updateAvatarUrl(String newAvatarUrl) {
        this.avatarUrl = newAvatarUrl;
    }
}
