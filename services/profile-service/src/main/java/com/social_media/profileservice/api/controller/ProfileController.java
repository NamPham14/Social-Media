package com.social_media.profileservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.profileservice.api.dto.ProfileResponse;
import com.social_media.profileservice.api.dto.UpdateProfileRequest;
import com.social_media.profileservice.application.command.CreateProfileCommand;
import com.social_media.profileservice.application.command.UpdateProfileCommand;
import com.social_media.profileservice.application.mapper.ProfileApiMapper;
import com.social_media.profileservice.application.usecase.*;

import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ProfileApiMapper profileMapper;
    private final UploadAvatarUseCase uploadAvatarUseCase;
    private final SearchProfilesUseCase searchProfilesUseCase;


    /**
     * API nội bộ dùng cho Identity Service gọi sang
     * @param command
     * @return
     */
    @PostMapping(ApiPath.INTERNAL + "/users")
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(@RequestBody CreateProfileCommand command) {

        Profile profile = createProfileUseCase.execute(command);

        ProfileResponse response = profileMapper.toResponse(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProfileResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .code(1000)
                        .message("Profile created successfully")
                        .data(response)
                        .build()
        );
    }


    /**
     * get detail profile of user
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@PathVariable("id") UUID id) {
        Profile profile = getProfileUseCase.execute(id);



        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Get profile successfully")
                .data(profileMapper.toResponse(profile))
                .build());
    }

    /**
     * update field profile
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @PathVariable("id") UUID id,
            @RequestBody UpdateProfileRequest request) {

        UpdateProfileCommand command = new UpdateProfileCommand(
                id,
                request.getFullName(),
                request.getBio(),
                request.getAvatarUrl()
        );

        Profile profile = updateProfileUseCase.execute(command);



        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Profile updated successfully")
                .data(profileMapper.toResponse(profile))
                .build());
    }

    /**
     * update avatar
     * @param id
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/users/{id}/avatar", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProfileResponse>> uploadAvatar(
            @PathVariable("id") UUID id,
            @RequestParam("file") MultipartFile file) throws Exception {

        Profile profile = uploadAvatarUseCase.execute(id, file);

        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .status(200)
                .code(1000)
                .message("Upload avatar thành công")
                .data(profileMapper.toResponse(profile))
                .build());
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<ProfileResponse>>> searchProfiles(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Profile> profiles = searchProfilesUseCase.execute(keyword, page, size);
        Page<ProfileResponse> responsePage = profiles.map(profileMapper::toResponse);

        return ResponseEntity.ok(ApiResponse.<Page<ProfileResponse>>builder()
                .status(200)
                .code(1000)
                .message("Tìm kiếm thành công")
                .data(responsePage)
                .build());
    }

}
