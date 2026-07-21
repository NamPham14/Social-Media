package com.social_media.postservice.application.usecase.post;

import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindPostsByAuthorIdUseCase {

    PostRepository postRepository;

    public Page<PostResponse> execute(UUID userId, Pageable pageable) {
        Page<Post> page = postRepository.findByAuthorId(userId, pageable);
        return page.map(PostResponse::from);
    }
}
