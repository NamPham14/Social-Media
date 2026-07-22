package com.social_media.chatservice.application.command;

import java.util.UUID;

public record MarkAsReadCommand(UUID conversationId, UUID currentUserId) {}
