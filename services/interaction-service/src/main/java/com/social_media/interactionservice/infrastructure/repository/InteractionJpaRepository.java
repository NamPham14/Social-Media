package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InteractionJpaRepository extends JpaRepository<Interaction, UUID> {
}
