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
import com.social_media.postservice.application.usecase.FindPostsByAuthorIdsUseCase;
import com.social_media.postservice.application.usecase.GetPostByPostIdUseCase;
import com.social_media.postservice.application.usecase.SearchPostsUseCase;
import com.social_media.postservice.application.usecase.UpdatePostUseCase;
import com.social_media.postservice.config.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class PostController {

    private final GetPostByPostIdUseCase getPostByPostIdUseCase;
    private final FindPostsByAuthorIdUseCase findPostsByAuthorIdUseCase;
    private final FindPostsByAuthorIdsUseCase findPostsByAuthorIdsUseCase;
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

    @GetMapping(ApiPath.POSTS_BY_AUTHORS)
    public ApiResponse<List<PostResponse>> getPostsByAuthorIds(
            @RequestParam("authorIds") List<UUID> authorIds,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> result = findPostsByAuthorIdsUseCase.execute(authorIds, pageable);
        return ApiResponse.<List<PostResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get Posts By AuthorIds Success")
                .data(result.getContent())
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
        CreatePostCommand command = CreatePostCommand.builder()
                .userId(SecurityUtils.getCurrentUserId())
                .caption(request.getCaption())
                .locationName(request.getLocationName())
                .images(request.getImages())
                .build();
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
                SecurityUtils.getCurrentUserId()
        );
        deletePostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Post Success")
                .build();
    }

    @PutMapping(value = ApiPath.POST_UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> updatePost(@Valid @ModelAttribute UpdatePostRequest request) {
        UpdatePostCommand command = UpdatePostCommand.builder()
                .id(request.getPostId())
                .userId(SecurityUtils.getCurrentUserId())
                .caption(request.getCaption())
                .locationName(request.getLocationName())
                .remainImageUrls(request.getRemainImageUrls())
                .newImages(request.getNewImages())
                .build();
        return ApiResponse.<PostResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Update Post Success")
                .data(updatePostUseCase.execute(command))
                .build();
    }
}
