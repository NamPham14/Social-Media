package com.social_media.postservice.application.service;

import com.social_media.postservice.application.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {

    List<UploadResponse> upload(List<MultipartFile> files);

    void deleteFile(String publicId);
}
