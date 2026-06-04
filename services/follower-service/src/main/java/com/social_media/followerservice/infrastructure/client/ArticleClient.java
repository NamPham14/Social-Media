package com.social_media.followerservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "article-service")
public interface ArticleClient {
    
    @GetMapping("/api/v1/articles/latest")
    Object getLatestPostsByAuthorIds(@RequestParam("authorIds") List<Long> authorIds, 
                                     @RequestParam("page") int page, 
                                     @RequestParam("size") int size);
}
