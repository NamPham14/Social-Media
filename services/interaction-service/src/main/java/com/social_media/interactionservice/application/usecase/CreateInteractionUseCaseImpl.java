package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateInteractionUseCaseImpl implements CreateInteractionUseCase {

    private final InteractionRepository interactionRepository;
    private final InteractionCounterRepository interactionCounterRepository;

    @Override
    @Transactional
    public InteractionResponse execute(CreateInteractionCommand command) {
        Interaction interaction = Interaction.create(
                command.userId(),
                command.targetType(),
                command.targetId(),
                command.reactionType()
        );

        try {
            Interaction saved = interactionRepository.save(interaction);
            interactionCounterRepository.increment(command.targetType(), command.targetId(), command.reactionType());

            return toResponse(saved, true);
        } catch (DataIntegrityViolationException ex) {
            return InteractionResponse.builder()
                    .userId(command.userId())
                    .targetType(command.targetType())
                    .targetId(command.targetId())
                    .reactionType(command.reactionType())
                    .created(false)
                    .duplicateIgnored(true)
                    .build();
        }
    }

    private InteractionResponse toResponse(Interaction interaction, boolean created) {
        return InteractionResponse.builder()
                .interactionId(interaction.getId())
                .userId(interaction.getUserId())
                .targetType(interaction.getTargetType())
                .targetId(interaction.getTargetId())
                .reactionType(interaction.getReactionType())
                .created(created)
                .duplicateIgnored(!created)
                .build();
    }
}
