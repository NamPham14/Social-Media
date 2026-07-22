package com.social_media.interactionservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "interactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_interaction_actor_target",
                        columnNames = {"user_id", "target_type", "target_id"}
                )
        }
)
@Getter
@NoArgsConstructor
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20, updatable = false)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false, updatable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 20, updatable = false)
    private ReactionType reactionType;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Interaction create(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type is required");
        }
        if (targetId == null) {
            throw new IllegalArgumentException("Target id is required");
        }
        if (reactionType == null) {
            throw new IllegalArgumentException("Reaction type is required");
        }

        Interaction interaction = new Interaction();
        interaction.userId = userId;
        interaction.targetType = targetType;
        interaction.targetId = targetId;
        interaction.reactionType = reactionType;
        interaction.deleted = false;
        return interaction;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
