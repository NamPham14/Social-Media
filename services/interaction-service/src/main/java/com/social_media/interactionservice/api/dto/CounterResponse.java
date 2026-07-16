package com.social_media.interactionservice.api.dto;

import com.social_media.interactionservice.domain.model.TargetType;
import java.util.UUID;

public record CounterResponse(TargetType targetType, UUID targetId, int likeCount, int clapCount) { }
