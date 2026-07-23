package com.social_media.postservice.application.usecase.post;

import com.social_media.postservice.application.command.ChangePostVisibilityCommand;
import com.social_media.postservice.application.exception.ResourceNotFoundException;
import com.social_media.postservice.domain.model.post.aggregate.Post;
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
                .orElseThrow(() -> new ResourceNotFoundException());

        if (!post.getUserId().equals(app.getUserId())) {
            throw new com.social_media.postservice.application.exception.UnauthorizedActionException();
        }

        post.changeVisibility(app.getNewStatus());
        postRepository.save(post);
    }
}
