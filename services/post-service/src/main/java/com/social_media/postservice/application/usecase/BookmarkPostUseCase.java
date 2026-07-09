package com.social_media.postservice.application.usecase;

import com.social_media.postservice.application.command.BookmarkPostCommand;
//import com.social_media.postservice.domain.model.bookmark.aggregate.Bookmark;
import com.social_media.postservice.domain.model.bookmark.aggregate.Bookmark;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.repository.BookmarkRepository;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkPostUseCase {

    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void execute(BookmarkPostCommand command) {
        Post post = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new com.social_media.postservice.application.exception.ResourceNotFoundException());

        if (bookmarkRepository.existsByUserIdAndPostId(command.getUserId(), command.getPostId())) {
            throw new com.social_media.postservice.application.exception.DuplicateResourceException();
        }

        Bookmark bookmark = Bookmark.create(command.getUserId(), command.getPostId());
        bookmarkRepository.save(bookmark);
    }
}

