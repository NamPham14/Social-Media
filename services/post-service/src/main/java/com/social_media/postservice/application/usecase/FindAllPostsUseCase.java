package com.social_media.postservice.application.usecase;

import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllPostsUseCase {

    PostRepository postRepository;

    public Page<PostResponse> execute(Pageable pageable) {
        Page<Post> page = postRepository.findAll(pageable);

        if (page.isEmpty()) {
            throw new com.social_media.postservice.application.exception.ResourceNotFoundException();
        }

        return page.map(PostResponse::from);
    }
}
