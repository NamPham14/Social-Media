package com.social_media.postservice.application.usecase;


import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.CreatePostCommand;
import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.application.dto.UploadResponse;
import com.social_media.postservice.application.service.MediaService;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.model.post.aggregate.Post;
//import com.social_media.postservice.domain.model.post.entity.PostMedia;
import com.social_media.postservice.domain.model.post.entity.PostMedia;
import com.social_media.postservice.domain.model.post.valueobject.MediaType;
import com.social_media.postservice.domain.repository.PostRepository;
//import com.social_media.postservice.domain.model.post.valueobject.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePostUseCase {

    private final MediaService mediaService;
    private final PostRepository postRepository;

    @Transactional
    public PostResponse execute(CreatePostCommand command) {

        Post post = Post.create(command.getUserId(), command.getCaption(), command.getLocationName());
        List<UploadResponse> uploads = mediaService.upload(command.getImages());

        int index = 0;
        for (UploadResponse file : uploads) {
            PostMedia media = PostMedia.create(
                    file.getPublicId(),
                    file.getUrl(),
                    MediaType.IMAGE,
                    index++
            );
            post.addMedia(media);
        }

        try {
            Post savedPost = postRepository.save(post);
            return PostResponse.from(savedPost);
        } catch (Exception e) {
            for (UploadResponse file : uploads) {
                try {
                    mediaService.deleteFile(file.getPublicId());
                } catch (Exception ex) {
                    log.error("Failed to rollback image on Cloudinary: " + file.getPublicId(), ex);
                }
            }
            throw new AppException(ErrorCode.CLOUDINARY_ERROR);
        }
    }
}
