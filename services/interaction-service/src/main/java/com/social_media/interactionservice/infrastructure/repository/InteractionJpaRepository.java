package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InteractionJpaRepository extends JpaRepository<Interaction, UUID> {
    @Modifying
    @Query(value = """
            INSERT INTO interactions (id, user_id, target_type, target_id, reaction_type, is_deleted, created_at)
            VALUES (:id, :actorId, :targetType, :targetId, :reactionType, false, NOW())
            ON CONFLICT (user_id, target_type, target_id, reaction_type) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(@Param("id") UUID id, @Param("actorId") UUID actorId,
                       @Param("targetType") String targetType, @Param("targetId") UUID targetId,
                       @Param("reactionType") String reactionType);

    @Modifying
    @Query("delete from Interaction i where i.userId = :actorId and i.targetType = :targetType and i.targetId = :targetId and i.reactionType = :reactionType")
    int remove(@Param("actorId") UUID actorId, @Param("targetType") TargetType targetType,
               @Param("targetId") UUID targetId, @Param("reactionType") ReactionType reactionType);

    Optional<Interaction> findByUserIdAndTargetTypeAndTargetIdAndReactionType(
            UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType);

    List<Interaction> findByUserIdAndTargetTypeAndTargetIdOrderByReactionTypeAsc(
            UUID actorId, TargetType targetType, UUID targetId);
}
