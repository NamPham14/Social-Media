package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UploadAvatarUseCase {
    Profile execute(UUID profileId, MultipartFile file) throws IOException;
}
