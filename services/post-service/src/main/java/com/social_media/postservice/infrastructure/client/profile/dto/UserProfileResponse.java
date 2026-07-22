package com.social_media.postservice.infrastructure.client.profile.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String id;
    private String username;
    private String fullName;
    private String avatarUrl;
}
