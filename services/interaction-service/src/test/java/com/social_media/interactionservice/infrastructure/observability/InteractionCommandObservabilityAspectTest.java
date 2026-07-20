package com.social_media.interactionservice.infrastructure.observability;

import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.usecase.CreateInteractionUseCase;
import com.social_media.interactionservice.domain.model.InteractionCounter;
import com.social_media.interactionservice.domain.model.InteractionCounterId;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InteractionCommandObservabilityAspectTest {

    private SimpleMeterRegistry meterRegistry;
    private InteractionCommandObservabilityAspect aspect;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        aspect = new InteractionCommandObservabilityAspect(meterRegistry);
        MDC.put("correlationId", UUID.randomUUID().toString());
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        meterRegistry.close();
    }

    @Test
    void duplicateCreateRecordsLatencyAndBoundedDuplicateTags() throws Throwable {
        UUID targetId = UUID.randomUUID();
        CreateInteractionCommand command = new CreateInteractionCommand(
                UUID.randomUUID(), TargetType.COMMENT, targetId, ReactionType.CLAP);
        InteractionResponse response = InteractionResponse.builder()
                .targetType(TargetType.COMMENT)
                .targetId(targetId)
                .reactionType(ReactionType.CLAP)
                .duplicateIgnored(true)
                .build();
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{command});
        when(joinPoint.proceed()).thenReturn(response);

        assertThat(aspect.observeCreate(joinPoint)).isSameAs(response);

        assertThat(meterRegistry.get(InteractionCommandObservabilityAspect.DURATION_METRIC)
                .tags("operation", "create", "outcome", "success").timer().count()).isEqualTo(1);
        var duplicate = meterRegistry.get(InteractionCommandObservabilityAspect.DUPLICATE_METRIC)
                .tags("target_type", "COMMENT", "reaction_type", "CLAP").counter();
        assertThat(duplicate.count()).isEqualTo(1);
        assertThat(duplicate.getId().getTags()).extracting(tag -> tag.getKey())
                .containsExactlyInAnyOrder("target_type", "reaction_type");
    }

    @Test
    void idempotentRemoveStillRecordsLatency() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{
                UUID.randomUUID(), TargetType.POST, UUID.randomUUID(), ReactionType.LIKE
        });
        when(joinPoint.proceed()).thenReturn(false);

        assertThat(aspect.observeRemove(joinPoint)).isEqualTo(false);

        assertThat(meterRegistry.get(InteractionCommandObservabilityAspect.DURATION_METRIC)
                .tags("operation", "remove", "outcome", "success").timer().count()).isEqualTo(1);
    }

    @Test
    void counterUpdateFailureIsCountedAndRethrown() throws Throwable {
        IllegalStateException failure = new IllegalStateException("counter write failed");
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn("increment");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{
                TargetType.POST, UUID.randomUUID(), ReactionType.LIKE
        });
        when(joinPoint.proceed()).thenThrow(failure);

        assertThatThrownBy(() -> aspect.observeCounterUpdate(joinPoint)).isSameAs(failure);

        assertThat(meterRegistry.get(InteractionCommandObservabilityAspect.COUNTER_FAILURE_METRIC)
                .tag("operation", "increment").counter().count()).isEqualTo(1);
    }

    @Test
    void pointcutsInterceptCommandAndCounterPortImplementations() {
        UUID targetId = UUID.randomUUID();
        InteractionResponse response = InteractionResponse.builder()
                .targetId(targetId)
                .targetType(TargetType.POST)
                .reactionType(ReactionType.LIKE)
                .created(true)
                .build();
        AspectJProxyFactory commandFactory = new AspectJProxyFactory(new StubCreateInteractionUseCase(response));
        commandFactory.addAspect(aspect);
        CreateInteractionUseCase commandProxy = commandFactory.getProxy();

        assertThat(commandProxy.execute(new CreateInteractionCommand(
                UUID.randomUUID(), TargetType.POST, targetId, ReactionType.LIKE))).isSameAs(response);

        AspectJProxyFactory counterFactory = new AspectJProxyFactory(new FailingCounterRepository());
        counterFactory.addAspect(aspect);
        InteractionCounterRepository counterProxy = counterFactory.getProxy();
        assertThatThrownBy(() -> counterProxy.increment(TargetType.POST, targetId, ReactionType.LIKE))
                .isInstanceOf(IllegalStateException.class);

        assertThat(meterRegistry.get(InteractionCommandObservabilityAspect.DURATION_METRIC)
                .tags("operation", "create", "outcome", "success").timer().count()).isEqualTo(1);
        assertThat(meterRegistry.get(InteractionCommandObservabilityAspect.COUNTER_FAILURE_METRIC)
                .tag("operation", "increment").counter().count()).isEqualTo(1);
    }

    private static final class StubCreateInteractionUseCase implements CreateInteractionUseCase {
        private final InteractionResponse response;

        private StubCreateInteractionUseCase(InteractionResponse response) {
            this.response = response;
        }

        @Override
        public InteractionResponse execute(CreateInteractionCommand command) {
            return response;
        }
    }

    private static final class FailingCounterRepository implements InteractionCounterRepository {
        @Override
        public void increment(TargetType targetType, UUID targetId, ReactionType reactionType) {
            throw new IllegalStateException("counter write failed");
        }

        @Override
        public void decrement(TargetType targetType, UUID targetId, ReactionType reactionType) {
        }

        @Override
        public Optional<InteractionCounter> find(TargetType targetType, UUID targetId) {
            return Optional.empty();
        }

        @Override
        public List<InteractionCounter> findAll(Collection<InteractionCounterId> ids) {
            return List.of();
        }

        @Override
        public int removeAllByTargets(TargetType targetType, Collection<UUID> targetIds) {
            return 0;
        }
    }
}
