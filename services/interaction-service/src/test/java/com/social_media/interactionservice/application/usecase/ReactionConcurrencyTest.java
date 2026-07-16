package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.port.out.TargetAvailabilityPort;
import com.social_media.interactionservice.domain.model.*;
import com.social_media.interactionservice.domain.repository.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.assertj.core.api.Assertions.assertThat;

class ReactionConcurrencyTest {
    @Test
    void oneHundredConcurrentIdenticalRequestsCreateOneLedgerEntryAndOneIncrement() throws Exception {
        UUID actor = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        AtomicBoolean inserted = new AtomicBoolean();
        AtomicInteger increments = new AtomicInteger();
        Interaction row = Interaction.create(actor, TargetType.POST, target, ReactionType.LIKE);

        InteractionRepository repository = new InteractionRepository() {
            public boolean insertIfAbsent(UUID a, TargetType t, UUID id, ReactionType r) {
                return inserted.compareAndSet(false, true);
            }
            public boolean remove(UUID a, TargetType t, UUID id, ReactionType r) { return false; }
            public Optional<Interaction> find(UUID a, TargetType t, UUID id, ReactionType r) { return Optional.of(row); }
            public List<Interaction> findActiveByActorAndTarget(UUID a, TargetType t, UUID id) { return List.of(row); }
        };
        InteractionCounterRepository counters = new InteractionCounterRepository() {
            public void increment(TargetType t, UUID id, ReactionType r) { increments.incrementAndGet(); }
            public void decrement(TargetType t, UUID id, ReactionType r) { }
            public Optional<InteractionCounter> find(TargetType t, UUID id) { return Optional.empty(); }
            public List<InteractionCounter> findAll(Collection<InteractionCounterId> ids) { return List.of(); }
        };
        TargetAvailabilityPort availability = (type, id, user) -> { };
        CreateInteractionUseCaseImpl useCase = new CreateInteractionUseCaseImpl(repository, counters, availability);
        CreateInteractionCommand command = new CreateInteractionCommand(actor, TargetType.POST, target, ReactionType.LIKE);
        ExecutorService pool = Executors.newFixedThreadPool(16);
        try {
            List<Callable<Boolean>> calls = java.util.stream.IntStream.range(0, 100)
                    .mapToObj(i -> (Callable<Boolean>) () -> useCase.execute(command).isCreated()).toList();
            long created = pool.invokeAll(calls).stream().filter(f -> {
                try { return f.get(); } catch (Exception e) { throw new RuntimeException(e); }
            }).count();
            assertThat(created).isEqualTo(1);
            assertThat(increments).hasValue(1);
        } finally {
            pool.shutdownNow();
        }
    }
}
