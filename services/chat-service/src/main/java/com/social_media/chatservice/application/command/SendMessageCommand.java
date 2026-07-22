package com.social_media.chatservice.application.command;

import com.social_media.chatservice.domain.model.enums.MessageType;

import java.util.UUID;

public record SendMessageCommand(UUID conversationId, UUID senderId, String content, MessageType type) {}
