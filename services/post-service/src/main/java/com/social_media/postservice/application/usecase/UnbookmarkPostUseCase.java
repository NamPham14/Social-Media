package com.social_media.postservice.application.usecase;

import com.social_media.postservice.application.command.UnbookmarkPostCommand;
//import com.social_media.postservice.domain.model.bookmark.aggregate.Bookmark;
import com.social_media.postservice.domain.model.bookmark.aggregate.Bookmark;
import com.social_media.postservice.domain.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnbookmarkPostUseCase {

    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void execute(UnbookmarkPostCommand command) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndPostId(command.getUserId(), command.getPostId())
                .orElseThrow(() -> new com.social_media.postservice.application.exception.ResourceNotFoundException());

        bookmarkRepository.delete(bookmark);
    }
}

