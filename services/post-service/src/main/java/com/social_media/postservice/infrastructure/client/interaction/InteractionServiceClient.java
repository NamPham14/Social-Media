package com.social_media.postservice.infrastructure.client.interaction;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.infrastructure.client.config.FeignClientConfig;
import com.social_media.postservice.infrastructure.client.interaction.dto.BatchCounterRequest;
import com.social_media.postservice.infrastructure.client.interaction.dto.CounterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "interaction-service",
        path = "/api/v1",
        configuration = FeignClientConfig.class
)
public interface InteractionServiceClient {

    @PostMapping("/interactions/counters/batch")
    ApiResponse<List<CounterResponse>> getCountersBatch(@RequestBody BatchCounterRequest request);
}
