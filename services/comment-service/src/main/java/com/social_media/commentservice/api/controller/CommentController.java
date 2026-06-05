package com.social_media.commentservice.api.controller;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.api.dto.CreateCommentRequest;
import com.social_media.commentservice.api.path.ApiPath;
import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.usecase.CreateCommentUseCase;
import com.social_media.commentservice.application.usecase.DeleteCommentUseCase;
import com.social_media.commentservice.application.usecase.FindCommentsByPostUseCase;
import com.social_media.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class CommentController {

    private final CreateCommentUseCase createCommentUseCase;
    private final FindCommentsByPostUseCase findCommentsByPostUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;

    @PostMapping(ApiPath.COMMENTS)
    public ApiResponse<CommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request) {
        CreateCommentCommand command = new CreateCommentCommand(
                request.getPostId(),
                request.getUserId(),
                request.getParentId(),
                request.getContent()
        );

        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create Comment Success")
                .data(createCommentUseCase.execute(command))
                .build();
    }

    @GetMapping(ApiPath.COMMENTS_BY_POST)
    public ApiResponse<List<CommentResponse>> getCommentsByPost(@PathVariable UUID postId) {
        return ApiResponse.<List<CommentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get Comments By Post Success")
                .data(findCommentsByPostUseCase.execute(postId))
                .build();
    }

    @DeleteMapping(ApiPath.COMMENT_BY_ID)
    public ApiResponse<Void> deleteComment(@PathVariable UUID commentId, @RequestParam UUID userId) {
        deleteCommentUseCase.execute(commentId, userId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Comment Success")
                .build();
    }
}
