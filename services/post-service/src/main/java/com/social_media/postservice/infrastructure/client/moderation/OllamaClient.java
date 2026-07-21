package com.social_media.postservice.infrastructure.client.moderation;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OllamaClient {

    private final RestClient restClient;

    public String analyze(String prompt) {

        Map<String, Object> request = Map.of(
                "model", "qwen2.5:3b",
                "prompt", prompt,
                "stream", false
        );

        Map response = restClient.post()
                .uri("http://localhost:11434/api/generate")
                .body(request)
                .retrieve()
                .body(Map.class);

        return (String) response.get("response");
    }
}
