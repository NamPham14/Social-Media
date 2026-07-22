package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.TargetReferenceRequest;
import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.model.InteractionCounter;
import com.social_media.interactionservice.domain.model.InteractionCounterId;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class GetInteractionSummariesUseCaseImplTest {
    @Test
    void anonymousBatchReturnsCountsWithoutQueryingActorLedger() {
        InteractionCounterRepository counters = mock(InteractionCounterRepository.class);
        InteractionRepository interactions = mock(InteractionRepository.class);
        GetInteractionSummariesUseCaseImpl useCase = new GetInteractionSummariesUseCaseImpl(counters, interactions);
        UUID targetId = UUID.randomUUID();

        var result = useCase.getBatch(null,
                List.of(new TargetReferenceRequest(TargetType.COMMENT, targetId)));

        assertThat(result.getFirst().likedByMe()).isFalse();
        verify(interactions, never()).findActiveByActorAndTargets(any(), any());
    }

    @Test
    void batchCombinesCountersAndLikedByMeWithoutPerTargetQueries() {
        InteractionCounterRepository counters = mock(InteractionCounterRepository.class);
        InteractionRepository interactions = mock(InteractionRepository.class);
        GetInteractionSummariesUseCaseImpl useCase = new GetInteractionSummariesUseCaseImpl(counters, interactions);
        UUID actor = UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        InteractionCounter firstCounter = mock(InteractionCounter.class);
        InteractionCounterId firstKey = new InteractionCounterId(TargetType.COMMENT, first);
        when(firstCounter.getId()).thenReturn(firstKey);
        when(firstCounter.getLikeCount()).thenReturn(7);
        when(counters.findAll(List.of(
                firstKey, new InteractionCounterId(TargetType.COMMENT, second))))
                .thenReturn(List.of(firstCounter));
        when(interactions.findActiveByActorAndTargets(actor, List.of(first, second)))
                .thenReturn(List.of(Interaction.create(actor, TargetType.COMMENT, first, ReactionType.LIKE)));

        var result = useCase.getBatch(actor, List.of(
                new TargetReferenceRequest(TargetType.COMMENT, first),
                new TargetReferenceRequest(TargetType.COMMENT, second)));

        assertThat(result).containsExactly(
                new com.social_media.interactionservice.api.dto.InteractionSummaryResponse(
                        TargetType.COMMENT, first, 7, true),
                new com.social_media.interactionservice.api.dto.InteractionSummaryResponse(
                        TargetType.COMMENT, second, 0, false));
        verify(counters).findAll(List.of(firstKey, new InteractionCounterId(TargetType.COMMENT, second)));
        verify(interactions).findActiveByActorAndTargets(actor, List.of(first, second));
    }

    @Test
    void sameUuidOnDifferentTargetTypeDoesNotLeakLikedState() {
        InteractionCounterRepository counters = mock(InteractionCounterRepository.class);
        InteractionRepository interactions = mock(InteractionRepository.class);
        GetInteractionSummariesUseCaseImpl useCase = new GetInteractionSummariesUseCaseImpl(counters, interactions);
        UUID actor = UUID.randomUUID();
        UUID sharedId = UUID.randomUUID();
        when(interactions.findActiveByActorAndTargets(actor, List.of(sharedId)))
                .thenReturn(List.of(Interaction.create(actor, TargetType.POST, sharedId, ReactionType.LIKE)));

        var result = useCase.getBatch(actor,
                List.of(new TargetReferenceRequest(TargetType.COMMENT, sharedId)));

        assertThat(result.getFirst().likedByMe()).isFalse();
    }
}
