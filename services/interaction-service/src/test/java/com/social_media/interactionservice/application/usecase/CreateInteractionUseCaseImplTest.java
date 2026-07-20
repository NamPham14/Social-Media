package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.event.ReactionNotificationEvent;
import com.social_media.interactionservice.application.port.out.InteractionEventOutbox;
import com.social_media.interactionservice.application.port.out.TargetAvailabilityPort;
import com.social_media.interactionservice.domain.model.*;
import com.social_media.interactionservice.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CreateInteractionUseCaseImplTest {
    private InteractionRepository interactions;
    private InteractionCounterRepository counters;
    private TargetAvailabilityPort availability;
    private InteractionEventOutbox outbox;
    private CreateInteractionUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        interactions = mock(InteractionRepository.class);
        counters = mock(InteractionCounterRepository.class);
        availability = mock(TargetAvailabilityPort.class);
        outbox = mock(InteractionEventOutbox.class);
        useCase = new CreateInteractionUseCaseImpl(interactions, counters, availability, outbox);
        when(availability.getAvailable(any(), any(), any())).thenAnswer(invocation ->
                new TargetAvailabilityPort.AvailableTarget(
                        invocation.getArgument(0), invocation.getArgument(1), UUID.randomUUID()));
    }

    @Test
    void newlyInsertedReactionIncrementsCounterOnce() {
        UUID actor = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        UUID interactionId = UUID.randomUUID();
        Interaction row = mock(Interaction.class);
        when(row.getId()).thenReturn(interactionId);
        when(row.getUserId()).thenReturn(actor);
        when(row.getTargetType()).thenReturn(TargetType.POST);
        when(row.getTargetId()).thenReturn(target);
        when(row.getReactionType()).thenReturn(ReactionType.LIKE);
        when(availability.getAvailable(TargetType.POST, target, actor))
                .thenReturn(new TargetAvailabilityPort.AvailableTarget(TargetType.POST, target, owner));
        when(interactions.insertIfAbsent(actor, TargetType.POST, target, ReactionType.LIKE)).thenReturn(true);
        when(interactions.find(actor, TargetType.POST, target, ReactionType.LIKE)).thenReturn(Optional.of(row));

        var result = useCase.execute(new CreateInteractionCommand(actor, TargetType.POST, target, ReactionType.LIKE));

        verify(availability).getAvailable(TargetType.POST, target, actor);
        verify(counters).increment(TargetType.POST, target, ReactionType.LIKE);
        verify(outbox).append(argThat((ReactionNotificationEvent event) ->
                event.interactionId().equals(interactionId) && event.recipientId().equals(owner)));
        assertThat(result.isCreated()).isTrue();
        assertThat(result.isDuplicateIgnored()).isFalse();
    }

    @Test
    void duplicateDoesNotIncrementCounter() {
        UUID actor = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        Interaction row = Interaction.create(actor, TargetType.COMMENT, target, ReactionType.CLAP);
        when(interactions.insertIfAbsent(actor, TargetType.COMMENT, target, ReactionType.CLAP)).thenReturn(false);
        when(interactions.find(actor, TargetType.COMMENT, target, ReactionType.CLAP)).thenReturn(Optional.of(row));

        var result = useCase.execute(new CreateInteractionCommand(actor, TargetType.COMMENT, target, ReactionType.CLAP));

        verify(counters, never()).increment(any(), any(), any());
        assertThat(result.isCreated()).isFalse();
        assertThat(result.isDuplicateIgnored()).isTrue();
        verify(outbox, never()).append(any(ReactionNotificationEvent.class));
    }

    @Test
    void selfReactionDoesNotCreateNotificationEvent() {
        UUID actor = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        Interaction row = Interaction.create(actor, TargetType.POST, target, ReactionType.LIKE);
        when(availability.getAvailable(TargetType.POST, target, actor))
                .thenReturn(new TargetAvailabilityPort.AvailableTarget(TargetType.POST, target, actor));
        when(interactions.insertIfAbsent(actor, TargetType.POST, target, ReactionType.LIKE)).thenReturn(true);
        when(interactions.find(actor, TargetType.POST, target, ReactionType.LIKE)).thenReturn(Optional.of(row));

        useCase.execute(new CreateInteractionCommand(actor, TargetType.POST, target, ReactionType.LIKE));

        verify(outbox, never()).append(any(ReactionNotificationEvent.class));
    }
}
