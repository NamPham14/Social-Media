package com.social_media.followerservice.infrastructure.client;

import com.social_media.common.api.ApiResponse;
import com.social_media.followerservice.infrastructure.client.config.FeignClientConfig;
import com.social_media.followerservice.infrastructure.client.dto.PostServicePostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "post-service", configuration = FeignClientConfig.class)
public interface ArticleClient {

    @GetMapping("/api/v1/posts/by-authors")
    ApiResponse<List<PostServicePostResponse>> getLatestPostsByAuthorIds(
            @RequestParam("authorIds") List<UUID> authorIds,
            @RequestParam("page") int page,
            @RequestParam("size") int size);
}
