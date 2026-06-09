package com.social_media.postservice.domain.repository;

import com.social_media.postservice.domain.aggregate.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository {

    Optional<Post> findById(UUID id);

    Page<Post> findByAuthorId(UUID userId, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

    Page<Post> searchByKeyword(String keyword, Pageable pageable);

    Post save(Post post);

    void delete(Post post);
}
