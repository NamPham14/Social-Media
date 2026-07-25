package com.social_media.profileservice.application.usecase.impl;

import com.social_media.profileservice.application.exception.ProfileNotFoundException;
import com.social_media.profileservice.application.service.MediaService;
import com.social_media.profileservice.application.usecase.UploadAvatarUseCase;
import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import com.social_media.profileservice.domain.repository.ProfileRepository;
import com.social_media.profileservice.domain.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class UploadAvatarUseCaseImpl implements UploadAvatarUseCase {
    private final ProfileRepository profileRepository;
    private final MediaService mediaService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
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
        
        Profile savedProfile = profileRepository.save(profile);

        // -------- OUTBOX PATTERN --------
        // Đóng gói thông tin để báo cho post-service biết Avatar đã thay đổi
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("userId", savedProfile.getId().value().toString());
        payload.put("authorName", savedProfile.getFullName());
        payload.put("authorAvatarUrl", savedProfile.getAvatarUrl());

        outboxEventRepository.save(
                savedProfile.getId().value().toString(),
                "PROFILE_UPDATED",
                payload.toString()
        );

        return savedProfile;
    }
}
