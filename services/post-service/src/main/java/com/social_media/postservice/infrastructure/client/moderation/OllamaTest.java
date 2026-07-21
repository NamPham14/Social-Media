package com.social_media.postservice.infrastructure.client.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// @Component
@RequiredArgsConstructor
@Slf4j
public class OllamaTest implements CommandLineRunner {

    private final OllamaClient ollamaClient;

    @Override
    public void run(String... args) {

        String result = ollamaClient.analyze("""
                Kiểm tra câu:
                "đồ ngu mày cút đi"

                Trả về DUY NHẤT JSON:
                {"violated": true/false}
                """);

        log.error("========== OLLAMA TEST RESULT =============================================================================================");
        log.error(result);
        log.error("===============================================================================================================================");
    }
}
