package com.social_media.postservice.application.usecase;


import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetPostByPostIdUseCase {

    PostRepository postRepository;

    public PostResponse execute(UUID postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new com.social_media.postservice.application.exception.ResourceNotFoundException());

        return PostResponse.from(post);
    }


}
