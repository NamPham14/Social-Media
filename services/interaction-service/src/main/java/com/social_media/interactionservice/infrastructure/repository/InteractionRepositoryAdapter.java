package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InteractionRepositoryAdapter implements InteractionRepository {

    private final InteractionJpaRepository interactionJpaRepository;

    @Override
    public Interaction save(Interaction interaction) {
        return interactionJpaRepository.saveAndFlush(interaction);
    }
}
