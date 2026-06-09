package com.social_media.postservice.application.usecase;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.DeletePostCommand;
import com.social_media.postservice.application.service.MediaService;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.aggregate.Post;
import com.social_media.postservice.domain.aggregate.PostMedia;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class DeletePostUseCase {

    PostRepository postRepository;
    MediaService mediaService;

    public void execute(DeletePostCommand command) {
        Post post = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        if (!post.getUserId().equals(command.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        post.softDelete();
        postRepository.save(post);

        List<PostMedia> postMediaList = new ArrayList<>(post.getMedias());
        for (PostMedia postMedia : postMediaList) {
            try {
                mediaService.deleteFile(postMedia.getPublicId());
            } catch (Exception e) {
                log.error("SoftDelete thành công nhưng chưa xóa được ảnh trên Cloudinary cho publicId: "
                        + postMedia.getPublicId(), e);
            }
        }
    }
}
