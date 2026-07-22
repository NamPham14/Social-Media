package com.social_media.chatservice.application.command;

import java.util.UUID;

public record DeleteMessageCommand(UUID messageId, UUID currentUserId) {}
