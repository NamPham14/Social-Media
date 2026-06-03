package com.social_media.postservice.application.usecase;
import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.UpdatePostCommand;
import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.application.dto.UploadResponse;
import com.social_media.postservice.application.service.MediaService;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.model.Post;
import com.social_media.postservice.domain.model.PostMedia;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UpdatePostUseCase {

    private final MediaService mediaService;
    private final PostRepository postRepository;

    @Transactional
    public PostResponse execute(UpdatePostCommand command) {

        Post post = postRepository.findById(command.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        post.update(command.getCaption(), command.getLocationName());

        List<String> remainImageUrls = command.getRemainImageUrls() != null ? command.getRemainImageUrls() : new ArrayList<>();

        List<PostMedia> mediasToDelete = new ArrayList<>();


        for (PostMedia media : post.getMedias()) {
            if (!remainImageUrls.contains(media.getMediaUrl())) {
                mediasToDelete.add(media);
            }
        }

        for (PostMedia postMedia : mediasToDelete) {
            post.removeMedia(postMedia);
        }

        List<UploadResponse> uploadsNew = mediaService.upload(command.getNewImages());

        int nextIndex = post.getMedias().size();
        for (UploadResponse file : uploadsNew) {
            PostMedia media = PostMedia.create(
                    file.getPublicId(),
                    file.getUrl(),
                    PostMedia.MediaType.IMAGE,
                    nextIndex++
            );
            post.addMedia(media);
        }

        try {
            Post savedPost = postRepository.save(post);

            for (PostMedia postMedia : mediasToDelete) {
                try {
                    mediaService.deleteFile(postMedia.getPublicId());
                } catch (Exception e) {
                    log.error("Lỗi xóa file ảnh cũ trên Cloudinary sau khi lưu DB: " + postMedia.getPublicId(), e);
                }
            }

            return PostResponse.from(savedPost);

        } catch (Exception e) {
            log.error("Lỗi lưu DB tại UpdatePost", e);
            for (UploadResponse file : uploadsNew) {
                try {
                    mediaService.deleteFile(file.getPublicId());
                } catch (Exception ex) {
                    log.error("Failed to rollback new image on Cloudinary: " + file.getPublicId(), ex);
                }
            }
            throw new AppException(ErrorCode.CLOUDINARY_ERROR);
        }
    }


}
