package com.social_media.profileservice.infrastructure.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social_media.profileservice.application.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryMediaService implements MediaService {
    private final Cloudinary cloudinary;
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) return;


            String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));


            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
