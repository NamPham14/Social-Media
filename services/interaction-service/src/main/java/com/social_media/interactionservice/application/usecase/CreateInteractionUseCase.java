package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;

public interface CreateInteractionUseCase {
    InteractionResponse execute(CreateInteractionCommand command);
}
