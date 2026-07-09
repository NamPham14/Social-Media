package com.social_media.interactionservice.domain.repository;

import com.social_media.interactionservice.domain.model.Interaction;

public interface InteractionRepository {
    Interaction save(Interaction interaction);
}
