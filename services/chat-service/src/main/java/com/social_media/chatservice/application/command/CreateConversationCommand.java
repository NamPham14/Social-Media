package com.social_media.chatservice.application.command;

import java.util.UUID;

public record CreateConversationCommand(UUID currentUserId, UUID otherUserId) {}
