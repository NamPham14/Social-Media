package com.social_media.chatservice.api.controller;

import com.social_media.chatservice.api.dto.SendMessageRequest;
import com.social_media.chatservice.api.dto.MessageResponse;
import com.social_media.chatservice.utils.path.ApiPath;
import com.social_media.chatservice.utils.mapper.ChatApiMapper;
import com.social_media.chatservice.application.usecase.DeleteMessageUseCase;
import com.social_media.chatservice.application.usecase.GetMessagesUseCase;
import com.social_media.chatservice.application.usecase.SendMessageUseCase;
import com.social_media.common.api.ApiResponse;
import com.social_media.common.api.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SendMessageUseCase sendMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;
    private final DeleteMessageUseCase deleteMessageUseCase;
    private final ChatApiMapper chatApiMapper;

    @PostMapping(ApiPath.CONVERSATION_MESSAGES)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MessageResponse> sendMessage(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request) {
        var message = sendMessageUseCase.execute(conversationId, currentUserId, request.content(), request.type());
        return ApiResponse.success(
                chatApiMapper.toMessageResponse(message, conversationId),
                "Message sent");
    }

    @GetMapping(ApiPath.CONVERSATION_MESSAGES)
    public ApiResponse<PageResponse<MessageResponse>> getMessages(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var messagePage = getMessagesUseCase.execute(conversationId, currentUserId, pageable);
        var responses = messagePage.map(m -> chatApiMapper.toMessageResponse(m, conversationId));
        return ApiResponse.success(PageResponse.of(responses), "Success");
    }

    @DeleteMapping(ApiPath.MESSAGE_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteMessage(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @PathVariable UUID messageId) {
        deleteMessageUseCase.execute(messageId, currentUserId);
        return ApiResponse.success("Message deleted");
    }
}
