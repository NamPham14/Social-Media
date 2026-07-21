package com.social_media.postservice.infrastructure.client.moderation.dto;


import java.util.List;

public record GeminiResponse(List<Candidate> candidates) {

    public record Candidate(Content content) {}

    public record Content(List<Part> parts) {}

    public record Part(String text) {}

    /**
     * Lấy phần text JSON mà Gemini trả về (do responseMimeType = application/json
     * nên field "text" ở đây chính là chuỗi JSON cần parse tiếp).
     */
    public String getJsonText() {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini response has no candidates");
        }
        Content content = candidates.get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            throw new IllegalStateException("Gemini response has no content parts");
        }
        return content.parts().get(0).text();
    }
}
