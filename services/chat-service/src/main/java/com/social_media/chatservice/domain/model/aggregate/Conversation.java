package com.social_media.chatservice.domain.model.aggregate;

import com.social_media.chatservice.domain.model.enums.ConversationType;
import com.social_media.chatservice.domain.model.valueobject.ConversationParticipant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Conversation {
    private Long id;
    private UUID conversationId;
    private ConversationType type;
    private String conversationKey;
    private List<ConversationParticipant> participants;
    private Instant createdAt;
    private Instant updatedAt;

    public Conversation(Long id, UUID conversationId, ConversationType type, String conversationKey,
                        List<ConversationParticipant> participants, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.type = type;
        this.conversationKey = conversationKey;
        this.participants = participants;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Conversation createOneToOne(UUID user1Id, UUID user2Id) {
        List<ConversationParticipant> participants = List.of(
                ConversationParticipant.create(user1Id),
                ConversationParticipant.create(user2Id)
        );
        String key = generateOneToOneKey(user1Id, user2Id);
        return new Conversation(null, UUID.randomUUID(), ConversationType.ONE_TO_ONE,
                key, participants, Instant.now(), Instant.now());
    }

    public static String generateOneToOneKey(UUID user1Id, UUID user2Id) {
        return user1Id.toString().compareTo(user2Id.toString()) < 0
                ? user1Id + "_" + user2Id
                : user2Id + "_" + user1Id;
    }

    public boolean hasParticipant(UUID userId) {
        return participants.stream().anyMatch(p -> p.userId().equals(userId));
    }

    public ConversationParticipant getParticipant(UUID userId) {
        return participants.stream()
                .filter(p -> p.userId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public void markAsRead(UUID userId, Instant readAt) {
        participants = participants.stream()
                .map(p -> p.userId().equals(userId) ? p.markAsRead(readAt) : p)
                .toList();
    }

    public Long getId() { return id; }
    public UUID getConversationId() { return conversationId; }
    public ConversationType getType() { return type; }
    public String getConversationKey() { return conversationKey; }
    public List<ConversationParticipant> getParticipants() { return participants; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
