package com.social_media.profileservice.application.usecase.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.social_media.profileservice.application.command.UpdateProfileCommand;
import com.social_media.profileservice.application.usecase.UpdateProfileUseCase;
import com.social_media.profileservice.application.exception.ProfileNotFoundException;
import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import com.social_media.profileservice.domain.repository.OutboxEventRepository;
import com.social_media.profileservice.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCaseImpl implements UpdateProfileUseCase {
    private final ProfileRepository profileRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Profile execute(UpdateProfileCommand command) {
        // Tìm Profile
        Profile profile = profileRepository.findById(command.id())
                .orElseThrow(ProfileNotFoundException::new);
        // Cập nhật thông tin
        profile.updateInfo(command.fullName(),command.bio(), command.avatarUrl());

        // Lưu xuống DB Profile
        Profile savedProfile = profileRepository.save(profile);


        // -------- OUTBOX PATTERN --------
        // Đóng gói 2 thông tin quan trọng nhất (Tên + Avatar) để báo cho các dịch vụ khác
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
