//package com.social_media.postservice.infrastructure.client.moderation.service;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.social_media.postservice.infrastructure.client.moderation.GeminiClient;
//import com.social_media.postservice.infrastructure.client.moderation.dto.GeminiRequest;
//import com.social_media.postservice.infrastructure.client.moderation.dto.GeminiResponse;
//import com.social_media.postservice.infrastructure.client.moderation.dto.ModerationScores;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//import io.github.resilience4j.retry.annotation.Retry;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class GeminiModerationHelper {
//
//    private final GeminiClient geminiClient;
//    private final ObjectMapper objectMapper;
//
//    private static final String SYSTEM_PROMPT = """
//        Bạn là hệ thống kiểm duyệt nội dung mạng xã hội tiếng Việt.
//        Nhiệm vụ: xác định đoạn text có vi phạm hay không (chửi bậy, ngôn từ kích động bạo lực,
//        thù ghét, quấy rối...), kể cả từ viết tắt, teencode, từ né kiểm duyệt (vd: d.m, vcđ, đm, clm, vl...).
//        Trả về DUY NHẤT 1 JSON object theo đúng schema, field "violated" = true nếu vi phạm, false nếu không.
//        """;
//
//    @Retry(name = "geminiRetry")
//    @CircuitBreaker(name = "geminiCircuitBreaker", fallbackMethod = "fallbackCheck")
//    public ModerationScores checkContent(String content) {
//        log.info("Đang gọi Gemini API để kiểm duyệt nội dung");
//
//        Map<String, Object> schema = Map.of(
//                "type", "OBJECT",
//                "properties", Map.of("violated", Map.of("type", "BOOLEAN")),
//                "required", List.of("violated")
//        );
//
//        GeminiRequest request = new GeminiRequest(
//                new GeminiRequest.SystemInstruction(List.of(new GeminiRequest.Part(SYSTEM_PROMPT))),
//                List.of(new GeminiRequest.Content(List.of(new GeminiRequest.Part(content)))),
//                new GeminiRequest.GenerationConfig("application/json", schema)
//        );
//
//        GeminiResponse response = geminiClient.analyze(request);
//
//        try {
//            return objectMapper.readValue(response.getJsonText(), ModerationScores.class);
//        } catch (Exception e) {
//            // lỗi parse JSON không phải lỗi network -> không nên retry vô ích,
//            // nhưng vẫn ném lên để CircuitBreaker/fallback xử lý thống nhất 1 chỗ
//            throw new IllegalStateException("Không parse được response từ Gemini", e);
//        }
//    }
//
//    /**
//     * Fallback khi hết retry hoặc circuit breaker OPEN.
//     * Trả về violated = false (an toàn: không tự động gỡ bài khi không chắc chắn được).
//     */
//    public ModerationScores fallbackCheck(String content, Throwable throwable) {
//        log.error("Fallback Active: Gemini Moderation lỗi cho content, coi như không vi phạm");
//        log.error("-> Nguyên nhân gốc: {}", throwable.getClass().getName(), throwable);
//        return new ModerationScores(false);
//    }
//}
