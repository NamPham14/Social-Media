package com.social_media.profileservice.application.usecase.impl;

import com.social_media.profileservice.application.exception.ProfileNotFoundException;
import com.social_media.profileservice.application.service.MediaService;
import com.social_media.profileservice.application.usecase.UploadAvatarUseCase;
import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import com.social_media.profileservice.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class UploadAvatarUseCaseImpl implements UploadAvatarUseCase {
    private final ProfileRepository profileRepository;
    private final MediaService mediaService;
    @Override
    public Profile execute(UUID profileId, MultipartFile file) throws IOException {


        String currentUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!profileId.toString().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền sửa ảnh đại diện của người khác!");
        }
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(ProfileNotFoundException::new);
        //Xóa ảnh cũ nếu có
        if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
            mediaService.deleteImage(profile.getAvatarUrl());
        }

        String imageUrl = mediaService.uploadImage(file);
        profile.updateAvatarUrl(imageUrl);
        return profileRepository.save(profile);
    }
}
