package com.social_media.profileservice.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileCommand {
    private UUID id;
    private String fullName;
    private String bio;
    private String avatarUrl;
}
