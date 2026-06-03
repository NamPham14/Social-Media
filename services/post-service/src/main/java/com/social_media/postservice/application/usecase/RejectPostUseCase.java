package com.social_media.postservice.application.usecase;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.RejectPostCommand;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.model.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RejectPostUseCase {

    private final PostRepository postRepository;

    @Transactional
    public void execute(RejectPostCommand command) {
        Post post = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        post.reject(command.getAdminId(), command.getReason());

        postRepository.save(post); // sau cần bắn noti sang noti-service
    }
}
