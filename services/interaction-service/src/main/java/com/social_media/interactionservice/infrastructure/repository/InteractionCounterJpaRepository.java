package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.InteractionCounter;
import com.social_media.interactionservice.domain.model.InteractionCounterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.Collection;
import com.social_media.interactionservice.domain.model.TargetType;

public interface InteractionCounterJpaRepository extends JpaRepository<InteractionCounter, InteractionCounterId> {

    @Modifying
    @Query(value = """
            INSERT INTO interaction_counters (target_type, target_id, like_count, updated_at)
            VALUES (:targetType, :targetId, 0, NOW())
            ON CONFLICT (target_type, target_id) DO NOTHING
            """, nativeQuery = true)
    void insertIfMissing(@Param("targetType") String targetType, @Param("targetId") UUID targetId);

    @Modifying
    @Query(value = """
            UPDATE interaction_counters
            SET like_count = like_count + 1,
                updated_at = NOW()
            WHERE target_type = :targetType
              AND target_id = :targetId
            """, nativeQuery = true)
    void increment(
            @Param("targetType") String targetType,
            @Param("targetId") UUID targetId
    );

    @Modifying
    @Query(value = """
            UPDATE interaction_counters
            SET like_count = GREATEST(like_count - 1, 0),
                updated_at = NOW()
            WHERE target_type = :targetType AND target_id = :targetId
            """, nativeQuery = true)
    void decrement(@Param("targetType") String targetType, @Param("targetId") UUID targetId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from InteractionCounter c where c.id.targetType = :targetType and c.id.targetId in :targetIds")
    int removeAllByTargets(@Param("targetType") TargetType targetType,
                           @Param("targetIds") Collection<UUID> targetIds);
}
