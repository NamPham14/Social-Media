package com.social_media.postservice.application.usecase;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.ChangePostVisibilityCommand;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.aggregate.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangePostVisibilityUseCase {

    private final PostRepository postRepository;

    public void execute(ChangePostVisibilityCommand app) {

        Post post = postRepository.findById(app.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        if (!post.getUserId().equals(app.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        post.changeVisibility(app.getNewStatus());
        postRepository.save(post);
    }
}
