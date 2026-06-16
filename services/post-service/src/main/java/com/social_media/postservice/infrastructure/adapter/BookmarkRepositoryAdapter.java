package com.social_media.postservice.infrastructure.adapter;

//import com.social_media.postservice.domain.model.bookmark.aggregate.Bookmark;
import com.social_media.postservice.domain.model.bookmark.aggregate.Bookmark;
import com.social_media.postservice.domain.repository.BookmarkRepository;
import com.social_media.postservice.infrastructure.entity.BookmarkEntity;
import com.social_media.postservice.infrastructure.mapper.BookmarkMapper;
import com.social_media.postservice.infrastructure.repository.BookmarkJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookmarkRepositoryAdapter implements BookmarkRepository {

    private final BookmarkJpaRepository bookmarkJpaRepository;
    private final BookmarkMapper bookmarkMapper;

    @Override
    public Optional<Bookmark> findById(UUID id) {
        return bookmarkJpaRepository.findById(id)
                .map(bookmarkMapper::toDomain);
    }

    @Override
    public Optional<Bookmark> findByUserIdAndPostId(UUID userId, UUID postId) {
        return bookmarkJpaRepository.findByUserIdAndPostId(userId, postId)
                .map(bookmarkMapper::toDomain);
    }

    @Override
    public Page<Bookmark> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable) {
        return bookmarkJpaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(bookmarkMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndPostId(UUID userId, UUID postId) {
        return bookmarkJpaRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public Bookmark save(Bookmark bookmark) {
        BookmarkEntity entity = bookmarkMapper.toEntity(bookmark);
        BookmarkEntity saved = bookmarkJpaRepository.save(entity);
        return bookmarkMapper.toDomain(saved);
    }

    @Override
    public void delete(Bookmark bookmark) {
        BookmarkEntity entity = bookmarkMapper.toEntity(bookmark);
        bookmarkJpaRepository.delete(entity);
    }
}

