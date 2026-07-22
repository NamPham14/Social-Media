package com.social_media.interactionservice.api.dto;

import com.social_media.interactionservice.domain.model.ReactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReactorResponse(UUID actorId, ReactionType reactionType, LocalDateTime reactedAt) { }
