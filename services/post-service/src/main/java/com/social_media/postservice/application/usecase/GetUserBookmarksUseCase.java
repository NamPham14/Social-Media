package com.social_media.postservice.application.usecase;

import com.social_media.postservice.domain.aggregate.Bookmark;
import com.social_media.postservice.domain.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserBookmarksUseCase {

    private final BookmarkRepository bookmarkRepository;

    public Page<Bookmark> execute(UUID userId, Pageable pageable) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
