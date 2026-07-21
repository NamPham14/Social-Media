package com.social_media.postservice.application.service;

import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.model.post.valueobject.ModerationResult;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostModerationApplier {

    private final PostRepository postRepository;

    @Transactional
    public void apply(UUID postId, ModerationResult result) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("Post not found: " + postId));

        post.applyModerationResult(result);
        postRepository.save(post);
    }
}