package com.social_media.postservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.api.dto.BookmarkRequest;
import com.social_media.postservice.api.dto.BookmarkResponse;
import com.social_media.postservice.api.path.ApiPath;
import com.social_media.postservice.application.command.BookmarkPostCommand;
import com.social_media.postservice.application.command.UnbookmarkPostCommand;
import com.social_media.postservice.application.usecase.BookmarkPostUseCase;
import com.social_media.postservice.application.usecase.GetUserBookmarksUseCase;
import com.social_media.postservice.application.usecase.UnbookmarkPostUseCase;
import com.social_media.postservice.config.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkPostUseCase bookmarkPostUseCase;
    private final UnbookmarkPostUseCase unbookmarkPostUseCase;
    private final GetUserBookmarksUseCase getUserBookmarksUseCase;

    @PostMapping(ApiPath.BOOKMARKS)
    public ApiResponse<Void> bookmarkPost(@Valid @RequestBody BookmarkRequest request) {
        BookmarkPostCommand command = BookmarkPostCommand.builder()
                .userId(SecurityUtils.getCurrentUserId())
                .postId(request.getPostId())
                .build();
        bookmarkPostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Bookmark Post Success")
                .build();
    }

    @DeleteMapping(ApiPath.BOOKMARK)
    public ApiResponse<Void> unbookmarkPost(@PathVariable("postId") UUID postId) {
        UnbookmarkPostCommand command = UnbookmarkPostCommand.builder()
                .userId(SecurityUtils.getCurrentUserId())
                .postId(postId)
                .build();
        unbookmarkPostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Unbookmark Post Success")
                .build();
    }

    @GetMapping(ApiPath.BOOKMARKS)
    public ApiResponse<Page<BookmarkResponse>> getUserBookmarks(Pageable pageable) {
        Page<BookmarkResponse> result = getUserBookmarksUseCase.execute(SecurityUtils.getCurrentUserId(), pageable)
                .map(BookmarkResponse::from);
        return ApiResponse.<Page<BookmarkResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get User Bookmarks Success")
                .data(result)
                .build();
    }
}
