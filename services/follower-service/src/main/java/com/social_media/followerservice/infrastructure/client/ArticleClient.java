package com.social_media.followerservice.infrastructure.client;

import com.social_media.followerservice.infrastructure.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "post-service", configuration = FeignClientConfig.class)
public interface ArticleClient {
    
    @GetMapping("/api/v1/articles/latest")
    Object getLatestPostsByAuthorIds(@RequestParam("authorIds") List<Long> authorIds, 
                                     @RequestParam("page") int page, 
                                     @RequestParam("size") int size);
}
