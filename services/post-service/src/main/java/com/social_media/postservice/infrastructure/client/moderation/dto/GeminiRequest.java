package com.social_media.postservice.infrastructure.client.moderation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record GeminiRequest(
        @JsonProperty("system_instruction") SystemInstruction systemInstruction,
        List<Content> contents,
        @JsonProperty("generationConfig") GenerationConfig generationConfig
) {
    public record SystemInstruction(List<Part> parts) {}

    public record Content(List<Part> parts) {}

    public record Part(String text) {}

    public record GenerationConfig(
            @JsonProperty("responseMimeType") String responseMimeType,
            @JsonProperty("responseSchema") Map<String, Object> responseSchema
    ) {}
}
