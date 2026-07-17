package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.InteractionCounterId;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DataJpaTest(properties = "spring.datasource.hikari.maximum-pool-size=20")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({InteractionRepositoryAdapter.class, InteractionCounterRepositoryAdapter.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class InteractionPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("interaction_integration")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private InteractionCounterRepository counterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE interactions, interaction_counters");
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS legacy_migration CASCADE");
    }

    @Test
    void oneHundredConcurrentDuplicatesCreateOneLedgerRowAndOneIncrement() throws Exception {
        UUID actorId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        AtomicInteger created = new AtomicInteger();
        List<Callable<Void>> requests = IntStream.range(0, 100)
                .mapToObj(index -> (Callable<Void>) () -> {
                    inTransaction(() -> {
                        if (interactionRepository.insertIfAbsent(
                                actorId, TargetType.POST, targetId, ReactionType.LIKE)) {
                            counterRepository.increment(TargetType.POST, targetId, ReactionType.LIKE);
                            created.incrementAndGet();
                        }
                    });
                    return null;
                }).toList();

        try (var executor = Executors.newFixedThreadPool(16)) {
            var futures = executor.invokeAll(requests);
            executor.shutdown();
            assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
            for (var future : futures) {
                future.get();
            }
        }

        assertThat(created).hasValue(1);
        assertThat(countLedgerRows(actorId, targetId, ReactionType.LIKE)).isEqualTo(1);
        assertThat(counterRepository.find(TargetType.POST, targetId)).hasValueSatisfying(counter -> {
            assertThat(counter.getLikeCount()).isEqualTo(1);
            assertThat(counter.getClapCount()).isZero();
        });
    }

    @Test
    void differentActorsIncrementTheSameTargetTwice() {
        UUID targetId = UUID.randomUUID();
        addReaction(UUID.randomUUID(), targetId, ReactionType.CLAP);
        addReaction(UUID.randomUUID(), targetId, ReactionType.CLAP);

        assertThat(counterRepository.find(TargetType.COMMENT, targetId))
                .hasValueSatisfying(counter -> assertThat(counter.getClapCount()).isEqualTo(2));
    }

    @Test
    void repeatedRemovalDecrementsOnceAndNeverBelowZero() {
        UUID actorId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        addReaction(actorId, targetId, ReactionType.LIKE);

        boolean first = removeReaction(actorId, targetId, ReactionType.LIKE);
        boolean second = removeReaction(actorId, targetId, ReactionType.LIKE);

        assertThat(first).isTrue();
        assertThat(second).isFalse();
        assertThat(counterRepository.find(TargetType.COMMENT, targetId))
                .hasValueSatisfying(counter -> assertThat(counter.getLikeCount()).isZero());
    }

    @Test
    void transactionRollbackKeepsLedgerAndCounterConsistent() {
        UUID actorId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        assertThatThrownBy(() -> inTransaction(() -> {
            interactionRepository.insertIfAbsent(actorId, TargetType.POST, targetId, ReactionType.LIKE);
            counterRepository.increment(TargetType.POST, targetId, ReactionType.LIKE);
            throw new IllegalStateException("force rollback");
        })).isInstanceOf(IllegalStateException.class);

        assertThat(countLedgerRows(actorId, targetId, ReactionType.LIKE)).isZero();
        assertThat(counterRepository.find(TargetType.POST, targetId)).isEmpty();
    }

    @Test
    void legacyBookmarkMigrationRemovesRowsAndCounterColumn() {
        jdbcTemplate.execute("CREATE SCHEMA legacy_migration");
        jdbcTemplate.execute("""
                CREATE TABLE legacy_migration.interactions (
                    id UUID PRIMARY KEY, user_id UUID NOT NULL, target_type VARCHAR(20) NOT NULL,
                    target_id UUID NOT NULL, reaction_type VARCHAR(20) NOT NULL,
                    is_deleted BOOLEAN NOT NULL DEFAULT FALSE, created_at TIMESTAMP NOT NULL DEFAULT NOW()
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE legacy_migration.interaction_counters (
                    target_type VARCHAR(20) NOT NULL, target_id UUID NOT NULL,
                    like_count INTEGER NOT NULL DEFAULT 0, clap_count INTEGER NOT NULL DEFAULT 0,
                    bookmark_count INTEGER NOT NULL DEFAULT 0, updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    PRIMARY KEY (target_type, target_id)
                )
                """);
        jdbcTemplate.update("""
                INSERT INTO legacy_migration.interactions
                    (id, user_id, target_type, target_id, reaction_type)
                VALUES (?, ?, 'POST', ?, 'BOOKMARK')
                """, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .defaultSchema("legacy_migration")
                .schemas("legacy_migration")
                .locations("classpath:db/migration/interaction")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load()
                .migrate();

        Integer bookmarks = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM legacy_migration.interactions WHERE reaction_type = 'BOOKMARK'", Integer.class);
        Integer bookmarkColumn = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM information_schema.columns
                WHERE table_schema = 'legacy_migration'
                  AND table_name = 'interaction_counters'
                  AND column_name = 'bookmark_count'
                """, Integer.class);
        assertThat(bookmarks).isZero();
        assertThat(bookmarkColumn).isZero();
    }

    private void addReaction(UUID actorId, UUID targetId, ReactionType reactionType) {
        inTransaction(() -> {
            if (interactionRepository.insertIfAbsent(actorId, TargetType.COMMENT, targetId, reactionType)) {
                counterRepository.increment(TargetType.COMMENT, targetId, reactionType);
            }
        });
    }

    private boolean removeReaction(UUID actorId, UUID targetId, ReactionType reactionType) {
        return new TransactionTemplate(transactionManager).execute(status -> {
            boolean removed = interactionRepository.remove(
                    actorId, TargetType.COMMENT, targetId, reactionType);
            if (removed) {
                counterRepository.decrement(TargetType.COMMENT, targetId, reactionType);
            }
            return removed;
        });
    }

    private int countLedgerRows(UUID actorId, UUID targetId, ReactionType reactionType) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM interactions
                WHERE user_id = ? AND target_id = ? AND reaction_type = ?
                """, Integer.class, actorId, targetId, reactionType.name());
        return count == null ? 0 : count;
    }

    private void inTransaction(Runnable action) {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> action.run());
    }
}
