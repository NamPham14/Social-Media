package com.social_media.profileservice.api;

import com.social_media.common.api.ApiResponse;
import com.social_media.profileservice.api.dto.ProfileResponse;
import com.social_media.profileservice.api.dto.UpdateProfileRequest;
import com.social_media.profileservice.application.command.CreateProfileCommand;
import com.social_media.profileservice.application.command.UpdateProfileCommand;
import com.social_media.profileservice.application.usecase.CreateProfileUseCase;
import com.social_media.profileservice.application.usecase.GetProfileUseCase;
import com.social_media.profileservice.application.usecase.UpdateProfileUseCase;
import com.social_media.profileservice.domain.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

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


    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@PathVariable("id") UUID id) {
        UserProfile profile = getProfileUseCase.execute(id);

        ProfileResponse response = ProfileResponse.builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .fullName(profile.getFullName())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .build();

        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Get profile successfully")
                .data(response)
                .build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @PathVariable("id") UUID id,
            @RequestBody UpdateProfileRequest request) {

        UpdateProfileCommand command = UpdateProfileCommand.builder()
                .id(id)
                .fullName(request.getFullName())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .build();

        UserProfile profile = updateProfileUseCase.execute(command);

        ProfileResponse response = ProfileResponse.builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .fullName(profile.getFullName())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .build();

        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Profile updated successfully")
                .data(response)
                .build());
    }

}
