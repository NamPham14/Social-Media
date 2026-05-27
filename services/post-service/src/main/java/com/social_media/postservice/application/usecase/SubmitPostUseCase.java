package com.social_media.postservice.application.usecase;


import com.social_media.postservice.application.command.SubmitPostCommand;
import com.social_media.postservice.domain.exception.ErrorPermission;
import com.social_media.postservice.domain.exception.NotFoundException;
import com.social_media.postservice.domain.model.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmitPostUseCase {

    private final PostRepository postRepository;

    @Transactional
    public void execute(SubmitPostCommand command) {
        Post post = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new NotFoundException("Cannot find post with id: " + command.getPostId()));

        if (!post.getUserId().equals(command.getUserId())) {
            throw new ErrorPermission("You don't have permission to perform this action");
        }

        post.submitForApproval();

        postRepository.save(post);
    }

}
