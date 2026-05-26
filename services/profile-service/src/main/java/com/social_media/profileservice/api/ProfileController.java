package com.social_media.profileservice.api;

import com.social_media.common.api.ApiResponse;
import com.social_media.profileservice.api.dto.ProfileResponse;
import com.social_media.profileservice.application.command.CreateProfileCommand;
import com.social_media.profileservice.application.usecase.CreateProfileUseCase;
import com.social_media.profileservice.domain.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;

    // API nội bộ dùng cho Identity Service gọi sang
    @PostMapping(ApiPath.INTERNAL + "/users")
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(@RequestBody CreateProfileCommand command) {
        
        UserProfile profile = createProfileUseCase.execute(command);

        ProfileResponse response = ProfileResponse.builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .fullName(profile.getFullName())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProfileResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .code(1000)
                        .message("Profile created successfully")
                        .data(response)
                        .build()
        );
    }
}
