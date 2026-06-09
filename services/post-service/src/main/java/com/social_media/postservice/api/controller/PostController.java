package com.social_media.postservice.api.controller;


import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.api.dto.CreatePostRequest;
import com.social_media.postservice.api.dto.DeletePostRequest;
import com.social_media.postservice.api.dto.UpdatePostRequest;
import com.social_media.postservice.api.path.ApiPath;
import com.social_media.postservice.application.command.DeletePostCommand;
import com.social_media.postservice.application.command.CreatePostCommand;
import com.social_media.postservice.application.command.UpdatePostCommand;
import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.application.usecase.CreatePostUseCase;
import com.social_media.postservice.application.usecase.DeletePostUseCase;
import com.social_media.postservice.application.usecase.FindAllPostsUseCase;
import com.social_media.postservice.application.usecase.FindPostsByAuthorIdUseCase;
import com.social_media.postservice.application.usecase.GetPostByPostIdUseCase;
import com.social_media.postservice.application.usecase.SearchPostsUseCase;
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
    private final CreatePostUseCase createPostUseCase;
    private final DeletePostUseCase deletePostUseCase;
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
    public ApiResponse<PostResponse> createPost(@Valid @ModelAttribute CreatePostRequest request) {
        CreatePostCommand command = new CreatePostCommand();
        command.setUserId(request.getUserId());
        command.setCaption(request.getCaption());
        command.setLocationName(request.getLocationName());
        command.setImages(request.getImages());
        return ApiResponse.<PostResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create Post Success")
                .data(createPostUseCase.execute(command))
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
}
