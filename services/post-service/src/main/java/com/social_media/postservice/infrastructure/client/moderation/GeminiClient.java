//package com.social_media.postservice.infrastructure.client.moderation;
//
//
//import com.social_media.postservice.infrastructure.client.moderation.dto.GeminiRequest;
//import com.social_media.postservice.infrastructure.client.moderation.dto.GeminiResponse;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClient;
//
////@Component
////@RequiredArgsConstructor
////public class GeminiClient {
////
////    private final RestClient restClient;
////
////    @Value("${gemini.api.key}")
////    private String apiKey;
////
////    public GeminiResponse analyze(GeminiRequest request) {
////        return restClient.post()
////                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={key}", apiKey)
////                .contentType(MediaType.APPLICATION_JSON)
////                .body(request)
////                .retrieve()
////                .body(GeminiResponse.class);
////        // lỗi HTTP (timeout, 4xx, 5xx...) sẽ tự ném RestClientException,
////        // để nguyên không catch ở đây — cho tầng Helper xử lý qua Retry/CircuitBreaker
////    }
////}
//
//
//@Component
//@RequiredArgsConstructor
//public class GeminiClient {
//
//    private final RestClient restClient;
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    @Value("${gemini.api.model}")
//    private String model;
//
//    public GeminiResponse analyze(GeminiRequest request) {
//        return restClient.post()
//                .uri("https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}",
//                        model, apiKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(request)
//                .retrieve()
//                .body(GeminiResponse.class);
//    }
//
//    @PostConstruct
//    public void init() {
//        System.out.println("Gemini model = " + model);
//    }
//
//}