package com.social_media.postservice.application.usecase;


import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.SubmitPostCommand;
import com.social_media.postservice.domain.exception.ErrorCode;
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
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!post.getUserId().equals(command.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        post.submitForApproval();

        postRepository.save(post);
    }

}
