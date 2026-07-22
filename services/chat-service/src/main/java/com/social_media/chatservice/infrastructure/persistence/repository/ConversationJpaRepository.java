package com.social_media.chatservice.infrastructure.persistence.repository;

import com.social_media.chatservice.infrastructure.persistence.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationJpaRepository extends JpaRepository<ConversationEntity, Long> {
    Optional<ConversationEntity> findByConversationId(UUID conversationId);

    @Query("SELECT c FROM ConversationEntity c JOIN c.participants p WHERE p.userId = :userId")
    List<ConversationEntity> findByParticipantUserId(@Param("userId") UUID userId);

    Optional<ConversationEntity> findByConversationKey(String conversationKey);
}
