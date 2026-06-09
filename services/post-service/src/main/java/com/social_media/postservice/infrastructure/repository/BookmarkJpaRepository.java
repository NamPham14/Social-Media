package com.social_media.postservice.infrastructure.repository;

import com.social_media.postservice.infrastructure.entity.BookmarkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookmarkJpaRepository extends JpaRepository<BookmarkEntity, UUID> {

    Optional<BookmarkEntity> findByUserIdAndPostId(UUID userId, UUID postId);

    Page<BookmarkEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    boolean existsByUserIdAndPostId(UUID userId, UUID postId);
}
