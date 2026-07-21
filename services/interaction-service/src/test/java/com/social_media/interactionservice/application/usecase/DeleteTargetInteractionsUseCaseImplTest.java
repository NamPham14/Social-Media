package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTargetInteractionsUseCaseImplTest {

    @Mock
    private InteractionRepository interactionRepository;
    @Mock
    private InteractionCounterRepository counterRepository;
    @InjectMocks
    private DeleteTargetInteractionsUseCaseImpl useCase;

    @Test
    void deletesInteractionsAndCountersForDistinctTargets() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        List<UUID> distinctIds = List.of(first, second);
        when(interactionRepository.removeAllByTargets(TargetType.COMMENT, distinctIds)).thenReturn(4);
        when(counterRepository.removeAllByTargets(TargetType.COMMENT, distinctIds)).thenReturn(2);

        var result = useCase.execute(TargetType.COMMENT, List.of(first, second, first));

        assertThat(result.interactionsDeleted()).isEqualTo(4);
        assertThat(result.countersDeleted()).isEqualTo(2);
        verify(interactionRepository).removeAllByTargets(TargetType.COMMENT, distinctIds);
        verify(counterRepository).removeAllByTargets(TargetType.COMMENT, distinctIds);
    }
}
