package com.social_media.identityservice.api.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class ProfileView {
    private UUID id;
    private String fullName;
    private String bio;
    private String avatarUrl;
}
