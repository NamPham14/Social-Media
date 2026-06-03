package com.social_media.postservice.application.usecase;


import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.ApprovePostCommand;
import com.social_media.postservice.domain.exception.ErrorCode;
//import com.social_media.postservice.domain.exception.NotFoundException;
import com.social_media.postservice.domain.model.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovePostUseCase {

    private final PostRepository postRepository;

    @Transactional
    public void execute(ApprovePostCommand command) {
//        Post post = postRepository.findById(command.getPostId())
//                .orElseThrow(() -> new NotFoundException("Not found post with id: " + command.getPostId()));

        Post post = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        post.approve(command.getAdminId());

        postRepository.save(post); // sau cần bắn noti sang noti-service
    }

}
