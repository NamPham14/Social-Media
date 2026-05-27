package com.social_media.postservice.application.usecase;

import com.social_media.postservice.application.command.ChangePostVisibilityCommand;
import com.social_media.postservice.domain.exception.ErrorPermission;
import com.social_media.postservice.domain.exception.NotFoundException;
import com.social_media.postservice.domain.model.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangePostVisibilityUseCase {

    private final PostRepository postRepository;

    public void execute(ChangePostVisibilityCommand app) {

        Post post = postRepository.findById(app.getPostId())
                .orElseThrow(() -> new NotFoundException("No post found with id: " + app.getPostId()));


        if (!post.getUserId().equals(app.getUserId())) {
            throw new ErrorPermission("You don't have permission to change post visibility");
        }

        post.changeVisibility(app.getNewStatus());
        postRepository.save(post);
    }
}
