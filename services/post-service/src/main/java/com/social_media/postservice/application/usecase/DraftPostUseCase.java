package com.social_media.postservice.application.usecase;


import com.social_media.postservice.application.command.DraftPostCommand;
import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.application.dto.UploadResponse;
import com.social_media.postservice.application.service.MediaService;
import com.social_media.postservice.domain.exception.ErrorCloudary;
import com.social_media.postservice.domain.model.Post;
import com.social_media.postservice.domain.model.PostMedia;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DraftPostUseCase {

    private final MediaService mediaService;
    private final PostRepository postRepository;

    @Transactional
    public PostResponse execute(DraftPostCommand command) {


        Post post = Post.draft(command.getUserId(), command.getCaption(), command.getLocationName());
        List<UploadResponse> uploads = mediaService.upload(command.getImages());

        int index = 0;
        for (UploadResponse file : uploads) {
            PostMedia media = PostMedia.create(
                    file.getPublicId(),
                    file.getUrl(),
                    PostMedia.MediaType.IMAGE,
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
            throw new ErrorCloudary("Cannot delete image on Cloudinary");
        }
    }
}
