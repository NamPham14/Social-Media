package com.social_media.interactionservice.api.dto;

import com.social_media.interactionservice.domain.model.TargetType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TargetReferenceRequest(@NotNull TargetType targetType, @NotNull UUID targetId) { }
