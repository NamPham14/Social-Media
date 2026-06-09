package com.social_media.postservice.application.usecase;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.UnbookmarkPostCommand;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.aggregate.Bookmark;
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
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        bookmarkRepository.delete(bookmark);
    }
}
