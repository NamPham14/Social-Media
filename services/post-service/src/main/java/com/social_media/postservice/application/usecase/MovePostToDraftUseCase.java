package com.social_media.postservice.application.usecase;


import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.MovePostToDraftCommand;
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
public class MovePostToDraftUseCase {

    private final PostRepository postRepository;

    @Transactional
    public void execute(MovePostToDraftCommand command) {
        Post post = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        if (!post.getUserId().equals(command.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        post.moveToDraft();
        postRepository.save(post);
    }

}
