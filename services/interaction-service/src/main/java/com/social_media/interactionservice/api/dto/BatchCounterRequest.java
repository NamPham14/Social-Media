package com.social_media.interactionservice.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BatchCounterRequest(@NotEmpty @Size(max = 100) List<@Valid TargetReferenceRequest> targets) { }
