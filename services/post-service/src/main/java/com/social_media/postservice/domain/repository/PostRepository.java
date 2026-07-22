package com.social_media.postservice.domain.repository;

import com.social_media.postservice.domain.model.post.aggregate.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {

    Optional<Post> findById(UUID id);

    Page<Post> findByAuthorId(UUID userId, Pageable pageable);

    Page<Post> findByAuthorIds(List<UUID> userIds, Pageable pageable); // HUY THÊM

    Page<Post> findAll(Pageable pageable);

    Page<Post> searchByKeyword(String keyword, Pageable pageable);

    Post save(Post post);

    void delete(Post post);

    Page<Post> findByAuthorIds(List<UUID> userIds, Pageable pageable);

    // hiếu thêm
    Page<Post> findAll(Pageable pageable, UUID viewerId, List<UUID> followingIds);

    // hiếu thêm
    Page<Post> findByAuthorId(UUID userId, Pageable pageable, UUID viewerId, List<UUID> followingIds);

    // hiếu thêm
    Page<Post> searchByKeyword(String keyword, Pageable pageable, UUID viewerId, List<UUID> followingIds);

    // hiếu thêm
    Page<Post> findByAuthorIds(List<UUID> userIds, Pageable pageable, UUID viewerId, List<UUID> followingIds);
}
