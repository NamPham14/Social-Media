package com.social_media.chatservice.api.controller;

import com.social_media.chatservice.api.dto.CreateConversationRequest;
import com.social_media.chatservice.api.dto.ConversationResponse;
import com.social_media.chatservice.utils.path.ApiPath;
import com.social_media.chatservice.utils.mapper.ChatApiMapper;
import com.social_media.chatservice.application.usecase.CreateConversationUseCase;
import com.social_media.chatservice.application.usecase.GetConversationDetailUseCase;
import com.social_media.chatservice.application.usecase.GetConversationsUseCase;
import com.social_media.chatservice.application.usecase.MarkAsReadUseCase;
import com.social_media.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPath.CONVERSATIONS)
@RequiredArgsConstructor
public class ConversationController {
    private final CreateConversationUseCase createConversationUseCase;
    private final GetConversationsUseCase getConversationsUseCase;
    private final GetConversationDetailUseCase getConversationDetailUseCase;
    private final MarkAsReadUseCase markAsReadUseCase;
    private final ChatApiMapper chatApiMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ConversationResponse> createConversation(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Valid @RequestBody CreateConversationRequest request) {
        var conversation = createConversationUseCase.execute(currentUserId, request.otherUserId());
        return ApiResponse.success(
                chatApiMapper.toConversationResponse(conversation, null, currentUserId),
                "Conversation created");
    }

    @GetMapping
    public ApiResponse<List<ConversationResponse>> getConversations(@RequestHeader("X-User-Id") UUID currentUserId) {
        var conversations = getConversationsUseCase.execute(currentUserId);
        var responses = conversations.stream()
                .map(c -> chatApiMapper.toConversationResponse(c, null, currentUserId))
                .toList();
        return ApiResponse.success(responses, "Success");
    }

    @GetMapping("/{conversationId}")
    public ApiResponse<ConversationResponse> getConversationDetail(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @PathVariable UUID conversationId) {
        var conversation = getConversationDetailUseCase.execute(conversationId, currentUserId);
        return ApiResponse.success(
                chatApiMapper.toConversationResponse(conversation, null, currentUserId),
                "Success");
    }

    @PatchMapping("/{conversationId}/read")
    public ApiResponse<Void> markAsRead(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @PathVariable UUID conversationId) {
        markAsReadUseCase.execute(conversationId, currentUserId);
        return ApiResponse.success("Marked as read");
    }
}
