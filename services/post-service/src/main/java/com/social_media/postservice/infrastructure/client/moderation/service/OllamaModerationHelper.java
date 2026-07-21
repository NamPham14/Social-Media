package com.social_media.postservice.infrastructure.client.moderation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.postservice.infrastructure.client.moderation.OllamaClient;
import com.social_media.postservice.infrastructure.client.moderation.dto.ModerationScores;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OllamaModerationHelper {

    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            Bạn là hệ thống kiểm duyệt nội dung mạng xã hội tiếng Việt.

            Nhiệm vụ:
            - Xác định đoạn text có vi phạm hay không.
            - Các loại vi phạm gồm: chửi bậy, xúc phạm, kích động bạo lực,
              thù ghét, quấy rối.
            - Hiểu được teencode, từ viết tắt, từ né kiểm duyệt
              (ví dụ: d.m, vcđ, đm, clm, vl...).

            Chỉ được trả về DUY NHẤT một JSON object.

            Format bắt buộc:
            {"violated": true}

            hoặc

            {"violated": false}
            """;


    @Retry(name = "ollamaRetry")
    @CircuitBreaker(
            name = "ollamaCircuitBreaker",
            fallbackMethod = "fallbackCheck"
    )
    public ModerationScores checkContent(String content) {

        log.info("Đang gọi Ollama API để kiểm duyệt nội dung");

        String prompt = """
                %s

                Nội dung cần kiểm tra:
                "%s"

                Trả về JSON:
                """.formatted(
                SYSTEM_PROMPT,
                content
        );


        String jsonResponse = ollamaClient.analyze(prompt);

        try {
            return objectMapper.readValue(
                    jsonResponse,
                    ModerationScores.class
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Không parse được response từ Ollama",
                    e
            );
        }
    }


    /**
     * Fallback khi Ollama lỗi hoặc circuit breaker OPEN.
     */
    public ModerationScores fallbackCheck(
            String content,
            Throwable throwable
    ) {

        log.error(
                "Fallback Active: Ollama Moderation lỗi, coi như không vi phạm"
        );

        log.error(
                "Nguyên nhân: {}",
                throwable.getClass().getName(),
                throwable
        );

        return new ModerationScores(false);
    }
}