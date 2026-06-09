package com.social_media.postservice.domain.repository;

import com.social_media.postservice.domain.aggreate.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository {

    Optional<Bookmark> findById(UUID id);

    Optional<Bookmark> findByUserIdAndPostId(UUID userId, UUID postId);

    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    boolean existsByUserIdAndPostId(UUID userId, UUID postId);

    Bookmark save(Bookmark bookmark);

    void delete(Bookmark bookmark);
}
