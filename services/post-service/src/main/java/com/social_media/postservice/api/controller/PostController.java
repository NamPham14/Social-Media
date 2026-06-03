package com.social_media.postservice.api.controller;


//import com.social_media.postservice.api.dto.ApiResponse;
import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.api.dto.ApprovePostRequest;
import com.social_media.postservice.api.dto.DeletePostRequest;
import com.social_media.postservice.api.dto.DraftPostRequest;
import com.social_media.postservice.api.dto.MovePostToDraftRequest;
import com.social_media.postservice.api.dto.RejectPostRequest;
import com.social_media.postservice.api.dto.SubmitPostRequest;
import com.social_media.postservice.api.dto.UpdatePostRequest;
import com.social_media.postservice.api.path.ApiPath;
import com.social_media.postservice.application.command.ApprovePostCommand;
import com.social_media.postservice.application.command.DeletePostCommand;
import com.social_media.postservice.application.command.DraftPostCommand;
import com.social_media.postservice.application.command.MovePostToDraftCommand;
import com.social_media.postservice.application.command.RejectPostCommand;
import com.social_media.postservice.application.command.SubmitPostCommand;
import com.social_media.postservice.application.command.UpdatePostCommand;
import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.application.usecase.ApprovePostUseCase;
import com.social_media.postservice.application.usecase.DeletePostUseCase;
import com.social_media.postservice.application.usecase.DraftPostUseCase;
import com.social_media.postservice.application.usecase.FindAllPostsUseCase;
import com.social_media.postservice.application.usecase.FindPostsByAuthorIdUseCase;
import com.social_media.postservice.application.usecase.GetPostByPostIdUseCase;
import com.social_media.postservice.application.usecase.MovePostToDraftUseCase;
import com.social_media.postservice.application.usecase.RejectPostUseCase;
import com.social_media.postservice.application.usecase.SearchPostsUseCase;
import com.social_media.postservice.application.usecase.SubmitPostUseCase;
import com.social_media.postservice.application.usecase.UpdatePostUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class PostController {

    private final GetPostByPostIdUseCase getPostByPostIdUseCase;
    private final FindPostsByAuthorIdUseCase findPostsByAuthorIdUseCase;
    private final FindAllPostsUseCase findAllPostsUseCase;
    private final SearchPostsUseCase searchPostsUseCase;
    private final DraftPostUseCase draftPostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final SubmitPostUseCase submitPostUseCase;
    private final MovePostToDraftUseCase movePostToDraftUseCase;
    private final ApprovePostUseCase approvePostUseCase;
    private final RejectPostUseCase rejectPostUseCase;
    private final UpdatePostUseCase updatePostUseCase;

    @GetMapping(ApiPath.POST_BY_ID)
    public ApiResponse<PostResponse> getPost(@PathVariable("postId") UUID postId) {
        return ApiResponse.<PostResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get Post Success")
                .data(getPostByPostIdUseCase.execute(postId))
                .build();
    }

    @GetMapping(ApiPath.POSTS_BY_AUTHOR)
    public ApiResponse<Page<PostResponse>> getPostsByAuthor(
            @PathVariable("userId") UUID userId, Pageable pageable) {
        return ApiResponse.<Page<PostResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get Posts By Author Success")
                .data(findPostsByAuthorIdUseCase.execute(userId, pageable))
                .build();
    }

    @GetMapping(ApiPath.POSTS)
    public ApiResponse<Page<PostResponse>> getAllPosts(Pageable pageable) {
        return ApiResponse.<Page<PostResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get All Posts Success")
                .data(findAllPostsUseCase.execute(pageable))
                .build();
    }

    @GetMapping(ApiPath.POSTS_SEARCH)
    public ApiResponse<Page<PostResponse>> searchPosts(
            @RequestParam("keyword") String keyword, Pageable pageable) {
        return ApiResponse.<Page<PostResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Search Posts Success")
                .data(searchPostsUseCase.execute(keyword, pageable))
                .build();
    }

    @PostMapping(value = ApiPath.POSTS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> draftPost(@Valid @ModelAttribute DraftPostRequest request) {
        DraftPostCommand command = new DraftPostCommand();
        command.setUserId(request.getUserId());
        command.setCaption(request.getCaption());
        command.setLocationName(request.getLocationName());
        command.setImages(request.getImages());
        return ApiResponse.<PostResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Draft Post Success")
                .data(draftPostUseCase.execute(command))
                .build();
    }

    @DeleteMapping(ApiPath.POST_DELETE)
    public ApiResponse<Void> deletePost(@Valid @RequestBody DeletePostRequest request) {
        DeletePostCommand command = new DeletePostCommand(
                request.getPostId(),
                request.getUserId()
        );
        deletePostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Post Success")
                .build();
    }

    @PutMapping(ApiPath.POST_SUBMIT)
    public ApiResponse<Void> submitPost(@Valid @RequestBody SubmitPostRequest request) {
        SubmitPostCommand command = new SubmitPostCommand(
                request.getPostId(),
                request.getUserId()
        );
        submitPostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Submit Post Success")
                .build();
    }

    @PutMapping(ApiPath.POST_MOVE_TO_DRAFT)
    public ApiResponse<Void> movePostToDraft(@Valid @RequestBody MovePostToDraftRequest request) {
        MovePostToDraftCommand command = new MovePostToDraftCommand(
                request.getPostId(),
                request.getUserId()
        );
        movePostToDraftUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Move Post To Draft Success")
                .build();
    }

    @PutMapping(value = ApiPath.POST_UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> updatePost(@Valid @ModelAttribute UpdatePostRequest request) {
        UpdatePostCommand command = new UpdatePostCommand();
        command.setId(request.getPostId());
        command.setUserId(request.getUserId());
        command.setCaption(request.getCaption());
        command.setLocationName(request.getLocationName());
        command.setRemainImageUrls(request.getRemainImageUrls());
        command.setNewImages(request.getNewImages());
        return ApiResponse.<PostResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Update Post Success")
                .data(updatePostUseCase.execute(command))
                .build();
    }

    @PutMapping(ApiPath.POST_APPROVE)
    public ApiResponse<Void> approvePost(@Valid @RequestBody ApprovePostRequest request) {
        ApprovePostCommand command = new ApprovePostCommand();
        command.setPostId(request.getPostId());
        command.setAdminId(request.getAdminId());
        approvePostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Approve Post Success")
                .build();
    }

    @PutMapping(ApiPath.POST_REJECT)
    public ApiResponse<Void> rejectPost(@Valid @RequestBody RejectPostRequest request) {
        RejectPostCommand command = new RejectPostCommand();
        command.setPostId(request.getPostId());
        command.setAdminId(request.getAdminId());
        command.setReason(request.getReason());
        rejectPostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Reject Post Success")
                .build();
    }

}
