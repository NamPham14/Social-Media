package com.social_media.commentservice.infrastructure.repository;

import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CommentRepositoryAdapter.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("comment_integration")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentJpaRepository jpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanDatabase() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    void flywayOwnsTheExpectedSchema() {
        Integer successfulMigrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success", Integer.class);
        Integer commentTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'comments'",
                Integer.class);
        Integer activeCountIndex = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM pg_indexes
                WHERE schemaname = 'public' AND indexname = 'idx_comments_active_post_id'
                """, Integer.class);

        Integer outboxTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'comment_outbox'",
                Integer.class);

        assertThat(successfulMigrations).isEqualTo(3);
        assertThat(commentTable).isEqualTo(1);
        assertThat(outboxTable).isEqualTo(1);
        assertThat(activeCountIndex).isEqualTo(1);
    }

    @Test
    void deletedParentRemainsVisibleOnlyWhileAnActiveReplyExists() {
        UUID postId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Comment parent = jpaRepository.saveAndFlush(Comment.create(postId, ownerId, null, "parent"));
        Comment reply = jpaRepository.saveAndFlush(
                Comment.create(postId, UUID.randomUUID(), parent.getId(), "reply"));

        parent.softDelete(ownerId);
        jpaRepository.saveAndFlush(parent);

        var discussion = commentRepository.findVisibleByPostId(postId, 0, 20);
        assertThat(discussion.content()).extracting(Comment::getId)
                .containsExactly(parent.getId(), reply.getId());

        reply.softDelete(reply.getUserId());
        jpaRepository.saveAndFlush(reply);

        var emptyDiscussion = commentRepository.findVisibleByPostId(postId, 0, 20);
        assertThat(emptyDiscussion.content()).isEmpty();
    }

    @Test
    void paginationReportsExactTotalsAndKeepsOldestFirst() {
        UUID postId = UUID.randomUUID();
        Comment first = jpaRepository.saveAndFlush(Comment.create(postId, UUID.randomUUID(), null, "first"));
        jpaRepository.saveAndFlush(Comment.create(postId, UUID.randomUUID(), null, "second"));
        jpaRepository.saveAndFlush(Comment.create(postId, UUID.randomUUID(), null, "third"));

        var page = commentRepository.findVisibleByPostId(postId, 0, 2);

        assertThat(page.totalElements()).isEqualTo(3);
        assertThat(page.totalPages()).isEqualTo(2);
        assertThat(page.content()).hasSize(2);
        assertThat(page.content().getFirst().getId()).isEqualTo(first.getId());
    }

    @Test
    void batchCountsOnlyActiveCommentsAndReplies() {
        UUID firstPost = UUID.randomUUID();
        UUID secondPost = UUID.randomUUID();
        UUID missingPost = UUID.randomUUID();
        Comment deletedParent = jpaRepository.saveAndFlush(
                Comment.create(firstPost, UUID.randomUUID(), null, "parent"));
        jpaRepository.saveAndFlush(
                Comment.create(firstPost, UUID.randomUUID(), deletedParent.getId(), "active reply"));
        deletedParent.softDelete(deletedParent.getUserId());
        jpaRepository.saveAndFlush(deletedParent);
        jpaRepository.saveAndFlush(Comment.create(secondPost, UUID.randomUUID(), null, "active comment"));

        var counts = commentRepository.countActiveByPostIds(List.of(firstPost, secondPost, missingPost));

        assertThat(counts).containsEntry(firstPost, 1L).containsEntry(secondPost, 1L);
        assertThat(counts).doesNotContainKey(missingPost);
        assertThat(commentRepository.countActiveByPostId(firstPost)).isEqualTo(1L);
        assertThat(commentRepository.countActiveByPostId(missingPost)).isZero();
    }

    @Test
    @Transactional
    void postDeletionSoftDeletesOnlyCommentsBelongingToThatPost() {
        UUID deletedPostId = UUID.randomUUID();
        UUID retainedPostId = UUID.randomUUID();
        Comment first = jpaRepository.saveAndFlush(
                Comment.create(deletedPostId, UUID.randomUUID(), null, "first"));
        Comment second = jpaRepository.saveAndFlush(
                Comment.create(deletedPostId, UUID.randomUUID(), first.getId(), "second"));
        jpaRepository.saveAndFlush(Comment.create(retainedPostId, UUID.randomUUID(), null, "retained"));

        assertThat(commentRepository.findActiveIdsByPostId(deletedPostId))
                .containsExactlyInAnyOrder(first.getId(), second.getId());
        assertThat(commentRepository.softDeleteAllByPostId(deletedPostId)).isEqualTo(2);

        assertThat(commentRepository.countActiveByPostId(deletedPostId)).isZero();
        assertThat(commentRepository.countActiveByPostId(retainedPostId)).isEqualTo(1);
        assertThat(commentRepository.softDeleteAllByPostId(deletedPostId)).isZero();
    }
}
