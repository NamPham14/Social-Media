package com.social_media.chatservice.api.controller;

import com.social_media.chatservice.api.dto.MessageResponse;
import com.social_media.chatservice.utils.mapper.ChatApiMapper;
import com.social_media.chatservice.application.usecase.SendMessageUseCase;
import com.social_media.chatservice.domain.model.enums.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final SendMessageUseCase sendMessageUseCase;
    private final ChatApiMapper chatApiMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(Principal principal, @Payload Map<String, Object> payload) {
        UUID senderId = UUID.fromString(principal.getName());
        UUID conversationId = UUID.fromString((String) payload.get("conversationId"));
        String content = (String) payload.get("content");
        MessageType type = MessageType.valueOf((String) payload.getOrDefault("type", "TEXT"));

        var message = sendMessageUseCase.execute(conversationId, senderId, content, type);
        MessageResponse response = chatApiMapper.toMessageResponse(message, conversationId);

        messagingTemplate.convertAndSend("/queue/messages/" + conversationId, response);
    }

    @MessageMapping("/chat.typing")
    public void typing(Principal principal, @Payload Map<String, Object> payload) {
        UUID userId = UUID.fromString(principal.getName());
        UUID conversationId = UUID.fromString((String) payload.get("conversationId"));
        boolean typing = Boolean.TRUE.equals(payload.get("typing"));

        messagingTemplate.convertAndSend(
                "/queue/typing/" + conversationId,
                Map.of("userId", userId.toString(), "typing", typing)
        );
    }
}
