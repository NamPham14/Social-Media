package com.social_media.postservice.infrastructure.client.comment;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.infrastructure.client.comment.dto.BatchCommentCountRequest;
import com.social_media.postservice.infrastructure.client.comment.dto.CommentCountResponse;
import com.social_media.postservice.infrastructure.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "comment-service",
        path = "/api/v1",
        configuration = FeignClientConfig.class
)
public interface CommentServiceClient {

    @PostMapping("/comments/counts/batch")
    ApiResponse<List<CommentCountResponse>> getCommentCounts(@RequestBody BatchCommentCountRequest request);
}
