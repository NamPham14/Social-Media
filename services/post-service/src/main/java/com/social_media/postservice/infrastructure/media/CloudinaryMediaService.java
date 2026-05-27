package com.social_media.postservice.infrastructure.media;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social_media.postservice.application.dto.UploadResponse;
import com.social_media.postservice.application.service.MediaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CloudinaryMediaService implements MediaService {

    Cloudinary cloudinary;

    @Override
    public List<UploadResponse> upload(List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<UploadResponse> results = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Map res = cloudinary.uploader()
                        .upload(file.getBytes(), ObjectUtils.emptyMap());

                results.add(
                        new UploadResponse(
                                res.get("secure_url").toString(),
                                res.get("public_id").toString()
                        )
                );

            } catch (Exception e) {
                throw new RuntimeException("Upload file failed", e);
            }
        }

        return results;
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader()
                    .destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Delete file failed", e);
        }
    }
}
