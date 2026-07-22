package com.social_media.chatservice.domain.model.aggregate;

import com.social_media.chatservice.domain.model.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private Long id;
    private UUID messageId;
    private Long conversationId;
    private UUID senderId;
    private String content;
    private MessageType type;
    private boolean deletedForSender;
    private Instant deletedAt;
    private Instant createdAt;

    public Message(Long id, UUID messageId, Long conversationId, UUID senderId, String content,
                   MessageType type, boolean deletedForSender, Instant deletedAt, Instant createdAt) {
        this.id = id;
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.deletedForSender = deletedForSender;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
    }

    public static Message send(Long conversationId, UUID senderId, String content, MessageType type) {
        return new Message(null, UUID.randomUUID(), conversationId, senderId,
                content, type, false, null, Instant.now());
    }

    public void deleteForSender(UUID userId) {
        if (!senderId.equals(userId)) {
            throw new IllegalStateException("Only the sender can delete this message");
        }
        this.deletedForSender = true;
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedForSender;
    }

    public boolean isSender(UUID userId) {
        return senderId.equals(userId);
    }

    public Long getId() { return id; }
    public UUID getMessageId() { return messageId; }
    public Long getConversationId() { return conversationId; }
    public UUID getSenderId() { return senderId; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }
    public boolean isDeletedForSender() { return deletedForSender; }
    public Instant getDeletedAt() { return deletedAt; }
    public Instant getCreatedAt() { return createdAt; }
}
