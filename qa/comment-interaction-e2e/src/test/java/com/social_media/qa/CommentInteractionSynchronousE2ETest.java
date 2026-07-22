package com.social_media.qa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.commentservice.CommentServiceApplication;
import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import com.social_media.interactionservice.InteractionServiceApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentInteractionSynchronousE2ETest {

    private static final String INTERNAL_TOKEN = "sync-e2e-contract-token";

    private final PostgreSQLContainer<?> commentDatabase = database("comment_sync_e2e");
    private final PostgreSQLContainer<?> interactionDatabase = database("interaction_sync_e2e");
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    private final ObjectMapper json = new ObjectMapper();

    private ConfigurableApplicationContext commentContext;
    private ConfigurableApplicationContext interactionContext;
    private String commentBaseUrl;
    private String interactionBaseUrl;

    @BeforeAll
    void startSystem() {
        Startables.deepStart(Stream.of(commentDatabase, interactionDatabase)).join();
        try {
            commentContext = startCommentService();
            commentBaseUrl = baseUrl(commentContext);
            interactionContext = startInteractionService(commentBaseUrl);
            interactionBaseUrl = baseUrl(interactionContext);
        } catch (RuntimeException failure) {
            stopSystem();
            throw failure;
        }
    }

    @AfterAll
    void stopSystem() {
        close(interactionContext);
        close(commentContext);
        interactionContext = null;
        commentContext = null;
        interactionDatabase.stop();
        commentDatabase.stop();
    }

    @Test
    void commentTargetFlowIsConsistentAcrossDuplicateDeleteAndDependencyFailure() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID commentOwner = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        Comment comment = commentContext.getBean(CommentRepository.class)
                .save(Comment.create(postId, commentOwner, null, "synchronous e2e target"));
        UUID commentId = comment.getId();
        String correlationId = UUID.randomUUID().toString();

        HttpResponse<String> created = createReaction(actorId, commentId, correlationId);
        assertStatus(created, 200);
        assertThat(data(created).path("created").asBoolean()).isTrue();
        assertThat(created.headers().firstValue("X-Correlation-Id")).contains(correlationId);

        HttpResponse<String> duplicate = createReaction(actorId, commentId, correlationId);
        assertStatus(duplicate, 200);
        assertThat(data(duplicate).path("duplicateIgnored").asBoolean()).isTrue();
        assertCounter(commentId, 1);

        assertThat(data(deleteReaction(actorId, commentId)).asBoolean()).isTrue();
        assertThat(data(deleteReaction(actorId, commentId)).asBoolean()).isFalse();
        assertCounter(commentId, 0);

        HttpResponse<String> deletedComment = request(
                "DELETE", commentBaseUrl + "/api/v1/comments/" + commentId,
                commentOwner, correlationId, null);
        assertStatus(deletedComment, 200);

        HttpResponse<String> rejected = createReaction(UUID.randomUUID(), commentId, correlationId);
        assertStatus(rejected, 404);
        assertThat(json.readTree(rejected.body()).path("traceId").asText()).isEqualTo(correlationId);
        assertCounter(commentId, 0);

        close(commentContext);
        commentContext = null;
        long startedAt = System.nanoTime();
        HttpResponse<String> unavailable = createReaction(UUID.randomUUID(), UUID.randomUUID(), correlationId);
        long elapsedMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
        assertStatus(unavailable, 503);
        assertThat(elapsedMillis).isLessThan(2_000L);
        assertCounter(commentId, 0);
    }

    private ConfigurableApplicationContext startCommentService() {
        return new SpringApplicationBuilder(CommentServiceApplication.class)
                .web(WebApplicationType.SERVLET)
                .logStartupInfo(false)
                .run(serviceArguments(commentDatabase,
                        "--spring.application.name=comment-service-e2e",
                        "--spring.flyway.locations=classpath:db/migration/comment",
                        "--internal.service-token=" + INTERNAL_TOKEN));
    }

    private ConfigurableApplicationContext startInteractionService(String commentUrl) {
        return new SpringApplicationBuilder(InteractionServiceApplication.class)
                .web(WebApplicationType.SERVLET)
                .logStartupInfo(false)
                .run(serviceArguments(interactionDatabase,
                        "--spring.application.name=interaction-service-e2e",
                        "--spring.flyway.locations=classpath:db/migration/interaction",
                        "--internal.service-token=" + INTERNAL_TOKEN,
                        "--messaging.topics.reaction-created=reaction-created-topic",
                        "--spring.cloud.openfeign.client.config.interactionCommentClient.url=" + commentUrl,
                        "--spring.cloud.openfeign.client.config.interactionCommentClient.connectTimeout=200",
                        "--spring.cloud.openfeign.client.config.interactionCommentClient.readTimeout=500",
                        "--resilience4j.retry.instances.commentAvailability.max-attempts=2",
                        "--resilience4j.retry.instances.commentAvailability.wait-duration=1ms",
                        "--resilience4j.retry.instances.commentAvailability.ignore-exceptions[0]="
                                + "com.social_media.interactionservice.domain.exception.TargetNotFoundException",
                        "--resilience4j.retry.instances.commentAvailability.ignore-exceptions[1]="
                                + "com.social_media.interactionservice.domain.exception.DependencyRejectedException",
                        "--resilience4j.circuitbreaker.instances.commentAvailability.minimum-number-of-calls=2",
                        "--resilience4j.circuitbreaker.instances.commentAvailability.sliding-window-size=2",
                        "--resilience4j.circuitbreaker.instances.commentAvailability.ignore-exceptions[0]="
                                + "com.social_media.interactionservice.domain.exception.TargetNotFoundException",
                        "--resilience4j.circuitbreaker.instances.commentAvailability.ignore-exceptions[1]="
                                + "com.social_media.interactionservice.domain.exception.DependencyRejectedException"));
    }

    private String[] serviceArguments(PostgreSQLContainer<?> database, String... additional) {
        String[] common = {
                "--server.port=0",
                "--spring.datasource.url=" + database.getJdbcUrl(),
                "--spring.datasource.username=" + database.getUsername(),
                "--spring.datasource.password=" + database.getPassword(),
                "--spring.jpa.hibernate.ddl-auto=validate",
                "--spring.jpa.show-sql=false",
                "--spring.jpa.open-in-view=false",
                "--spring.cloud.discovery.enabled=false",
                "--eureka.client.enabled=false",
                "--eureka.client.register-with-eureka=false",
                "--eureka.client.fetch-registry=false",
                "--messaging.outbox.enabled=false",
                "--management.endpoints.enabled-by-default=false",
                "--logging.level.root=WARN"
        };
        String[] arguments = new String[common.length + additional.length];
        System.arraycopy(common, 0, arguments, 0, common.length);
        System.arraycopy(additional, 0, arguments, common.length, additional.length);
        return arguments;
    }

    private HttpResponse<String> createReaction(UUID actorId, UUID commentId, String correlationId)
            throws Exception {
        String body = """
                {"targetType":"COMMENT","targetId":"%s","reactionType":"LIKE"}
                """.formatted(commentId);
        return request("POST", interactionBaseUrl + "/api/v1/interactions", actorId, correlationId, body);
    }

    private HttpResponse<String> deleteReaction(UUID actorId, UUID commentId) throws Exception {
        return request("DELETE", interactionBaseUrl + "/api/v1/interactions/COMMENT/" + commentId + "/LIKE",
                actorId, UUID.randomUUID().toString(), null);
    }

    private void assertCounter(UUID commentId, int expectedLikes) throws Exception {
        HttpResponse<String> response = request(
                "GET", interactionBaseUrl + "/api/v1/interactions/counters/COMMENT/" + commentId,
                null, UUID.randomUUID().toString(), null);
        assertStatus(response, 200);
        assertThat(data(response).path("reactionCount").asInt()).isEqualTo(expectedLikes);
    }

    private HttpResponse<String> request(
            String method, String url, UUID actorId, String correlationId, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("X-Correlation-Id", correlationId);
        if (actorId != null) {
            builder.header("X-Auth-User-Id", actorId.toString());
        }
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode data(HttpResponse<String> response) throws Exception {
        return json.readTree(response.body()).path("data");
    }

    private void assertStatus(HttpResponse<String> response, int expected) {
        assertThat(response.statusCode())
                .withFailMessage("Expected HTTP %s but received %s: %s", expected, response.statusCode(), response.body())
                .isEqualTo(expected);
    }

    private String baseUrl(ConfigurableApplicationContext context) {
        int port = ((WebServerApplicationContext) context).getWebServer().getPort();
        return "http://127.0.0.1:" + port;
    }

    private void close(ConfigurableApplicationContext context) {
        if (context != null && context.isActive()) {
            context.close();
        }
    }

    private static PostgreSQLContainer<?> database(String name) {
        return new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName(name)
                .withUsername("test")
                .withPassword("test");
    }
}
