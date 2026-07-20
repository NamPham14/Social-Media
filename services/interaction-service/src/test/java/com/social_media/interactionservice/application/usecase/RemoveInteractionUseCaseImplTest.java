package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.domain.model.*;
import com.social_media.interactionservice.domain.repository.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RemoveInteractionUseCaseImplTest {
    @Test
    void repeatedRemoveNeverDecrementsTwice() {
        InteractionRepository interactions = mock(InteractionRepository.class);
        InteractionCounterRepository counters = mock(InteractionCounterRepository.class);
        UUID actor = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        when(interactions.remove(actor, TargetType.POST, target, ReactionType.LIKE)).thenReturn(true, false);
        RemoveInteractionUseCaseImpl useCase = new RemoveInteractionUseCaseImpl(interactions, counters);

        assertThat(useCase.execute(actor, TargetType.POST, target, ReactionType.LIKE)).isTrue();
        assertThat(useCase.execute(actor, TargetType.POST, target, ReactionType.LIKE)).isFalse();
        verify(counters, times(1)).decrement(TargetType.POST, target, ReactionType.LIKE);
    }
}
