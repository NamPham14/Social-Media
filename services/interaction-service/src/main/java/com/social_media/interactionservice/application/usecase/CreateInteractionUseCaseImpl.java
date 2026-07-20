package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.event.ReactionNotificationEvent;
import com.social_media.interactionservice.application.port.out.InteractionEventOutbox;
import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.application.port.out.TargetAvailabilityPort;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateInteractionUseCaseImpl implements CreateInteractionUseCase {

    private final InteractionRepository interactionRepository;
    private final InteractionCounterRepository interactionCounterRepository;
    private final TargetAvailabilityPort targetAvailabilityPort;
    private final InteractionEventOutbox eventOutbox;

    @Override
    @Transactional
    public InteractionResponse execute(CreateInteractionCommand command) {
        TargetAvailabilityPort.AvailableTarget target = targetAvailabilityPort.getAvailable(
                command.targetType(), command.targetId(), command.actorId());
        boolean created = interactionRepository.insertIfAbsent(
                command.actorId(), command.targetType(), command.targetId(), command.reactionType());
        if (created) {
            interactionCounterRepository.increment(command.targetType(), command.targetId(), command.reactionType());
        }
        Interaction interaction = interactionRepository.find(command.actorId(), command.targetType(),
                        command.targetId(), command.reactionType())
                .orElseThrow(() -> new IllegalStateException("Reaction ledger insert was not observable"));
        if (created && !interaction.getUserId().equals(target.ownerId())) {
            eventOutbox.append(ReactionNotificationEvent.from(interaction, target.ownerId()));
        }
        return toResponse(interaction, created);
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
