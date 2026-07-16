package com.social_media.commentservice.api.controller;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.api.dto.CreateCommentRequest;
import com.social_media.commentservice.api.dto.UpdateCommentRequest;
import com.social_media.commentservice.api.dto.PageResponse;
import com.social_media.commentservice.api.path.ApiPath;
import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.usecase.CreateCommentUseCase;
import com.social_media.commentservice.application.usecase.DeleteCommentUseCase;
import com.social_media.commentservice.application.usecase.FindCommentsByPostUseCase;
import com.social_media.commentservice.application.usecase.GetCommentUseCase;
import com.social_media.commentservice.application.usecase.UpdateCommentUseCase;
import com.social_media.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class CommentController {

    private final CreateCommentUseCase createCommentUseCase;
    private final FindCommentsByPostUseCase findCommentsByPostUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final UpdateCommentUseCase updateCommentUseCase;
    private final GetCommentUseCase getCommentUseCase;

    @PostMapping(ApiPath.COMMENTS)
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @Valid @RequestBody CreateCommentRequest request) {
        CreateCommentCommand command = new CreateCommentCommand(
                request.getPostId(),
                actorId,
                request.getParentId(),
                request.getContent()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED.value())
                .message("Create Comment Success")
                .data(createCommentUseCase.execute(command))
                .build());
    }

    @GetMapping(ApiPath.COMMENTS_BY_POST)
    public ApiResponse<PageResponse<CommentResponse>> getCommentsByPost(
            @PathVariable UUID postId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.value())
                .message("Get Comments By Post Success")
                .data(findCommentsByPostUseCase.execute(postId, Math.max(page, 0), safeSize))
                .build();
    }

    @DeleteMapping(ApiPath.COMMENT_BY_ID)
    public ApiResponse<Void> deleteComment(
            @PathVariable UUID commentId,
            @RequestHeader("X-Auth-User-Id") UUID actorId) {
        deleteCommentUseCase.execute(commentId, actorId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.value())
                .message("Delete Comment Success")
                .build();
    }

    @PatchMapping(ApiPath.COMMENT_BY_ID)
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable UUID commentId,
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @Valid @RequestBody UpdateCommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.OK.value()).status(HttpStatus.OK.value())
                .message("Update Comment Success")
                .data(updateCommentUseCase.execute(commentId, actorId, request.getContent()))
                .build();
    }

    @GetMapping(ApiPath.COMMENT_BY_ID)
    public ApiResponse<CommentResponse> getComment(@PathVariable UUID commentId) {
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.OK.value()).status(HttpStatus.OK.value())
                .message("Get Comment Success").data(getCommentUseCase.execute(commentId)).build();
    }
}
