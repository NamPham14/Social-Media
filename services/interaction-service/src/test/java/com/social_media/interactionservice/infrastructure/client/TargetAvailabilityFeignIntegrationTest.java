package com.social_media.interactionservice.infrastructure.client;

import com.social_media.interactionservice.domain.exception.DependencyRejectedException;
import com.social_media.interactionservice.domain.exception.DependencyUnavailableException;
import com.social_media.interactionservice.domain.exception.ReactionConflictException;
import com.social_media.interactionservice.domain.exception.TargetNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        classes = TargetAvailabilityFeignIntegrationTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.cloud.discovery.enabled=false",
                "eureka.client.enabled=false",
                "internal.service-token=contract-test-token",
                "spring.cloud.openfeign.client.config.interactionPostClient.url=http://localhost:${wiremock.server.port}",
                "spring.cloud.openfeign.client.config.interactionCommentClient.url=http://localhost:${wiremock.server.port}",
                "spring.cloud.openfeign.client.config.interactionPostClient.connectTimeout=100",
                "spring.cloud.openfeign.client.config.interactionPostClient.readTimeout=500",
                "spring.cloud.openfeign.client.config.interactionCommentClient.connectTimeout=100",
                "spring.cloud.openfeign.client.config.interactionCommentClient.readTimeout=500",
                "resilience4j.retry.instances.postAvailability.max-attempts=2",
                "resilience4j.retry.instances.postAvailability.wait-duration=1ms",
                "resilience4j.retry.instances.postAvailability.ignore-exceptions[0]=com.social_media.interactionservice.domain.exception.TargetNotFoundException",
                "resilience4j.retry.instances.postAvailability.ignore-exceptions[1]=com.social_media.interactionservice.domain.exception.ReactionConflictException",
                "resilience4j.retry.instances.postAvailability.ignore-exceptions[2]=com.social_media.interactionservice.domain.exception.DependencyRejectedException",
                "resilience4j.retry.instances.commentAvailability.max-attempts=2",
                "resilience4j.retry.instances.commentAvailability.wait-duration=1ms",
                "resilience4j.retry.instances.commentAvailability.ignore-exceptions[0]=com.social_media.interactionservice.domain.exception.TargetNotFoundException",
                "resilience4j.retry.instances.commentAvailability.ignore-exceptions[1]=com.social_media.interactionservice.domain.exception.DependencyRejectedException",
                "resilience4j.circuitbreaker.instances.postAvailability.sliding-window-size=2",
                "resilience4j.circuitbreaker.instances.postAvailability.minimum-number-of-calls=2",
                "resilience4j.circuitbreaker.instances.postAvailability.failure-rate-threshold=50",
                "resilience4j.circuitbreaker.instances.postAvailability.wait-duration-in-open-state=1h",
                "resilience4j.circuitbreaker.instances.postAvailability.ignore-exceptions[0]=com.social_media.interactionservice.domain.exception.TargetNotFoundException",
                "resilience4j.circuitbreaker.instances.postAvailability.ignore-exceptions[1]=com.social_media.interactionservice.domain.exception.ReactionConflictException",
                "resilience4j.circuitbreaker.instances.postAvailability.ignore-exceptions[2]=com.social_media.interactionservice.domain.exception.DependencyRejectedException",
                "resilience4j.circuitbreaker.instances.commentAvailability.sliding-window-size=2",
                "resilience4j.circuitbreaker.instances.commentAvailability.minimum-number-of-calls=2",
                "resilience4j.circuitbreaker.instances.commentAvailability.failure-rate-threshold=50",
                "resilience4j.circuitbreaker.instances.commentAvailability.wait-duration-in-open-state=1h",
                "resilience4j.circuitbreaker.instances.commentAvailability.ignore-exceptions[0]=com.social_media.interactionservice.domain.exception.TargetNotFoundException",
                "resilience4j.circuitbreaker.instances.commentAvailability.ignore-exceptions[1]=com.social_media.interactionservice.domain.exception.DependencyRejectedException"
        })
@AutoConfigureWireMock(port = 0)
class TargetAvailabilityFeignIntegrationTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            FlywayAutoConfiguration.class
    })
    @EnableFeignClients(clients = {PostClient.class, CommentClient.class})
    @Import({PostAvailabilityChecker.class, CommentAvailabilityChecker.class})
    static class TestApplication {
    }

    private final PostAvailabilityChecker postChecker;
    private final CommentAvailabilityChecker commentChecker;
    private final CircuitBreaker postCircuit;
    private final CircuitBreaker commentCircuit;

    @Autowired
    TargetAvailabilityFeignIntegrationTest(
            PostAvailabilityChecker postChecker,
            CommentAvailabilityChecker commentChecker,
            CircuitBreakerRegistry circuitBreakerRegistry) {
        this.postChecker = postChecker;
        this.commentChecker = commentChecker;
        this.postCircuit = circuitBreakerRegistry.circuitBreaker("postAvailability");
        this.commentCircuit = circuitBreakerRegistry.circuitBreaker("commentAvailability");
    }

    @BeforeEach
    void resetDependencies() {
        reset();
        postCircuit.reset();
        commentCircuit.reset();
    }

    @AfterEach
    void clearCorrelationContext() {
        MDC.clear();
    }

    @Test
    void postCheckPropagatesActorAndCorrelationHeaders() {
        UUID postId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId))
                .willReturn(okJson(publicPostResponse(postId))));
        MDC.put("correlationId", correlationId);

        assertThatCode(() -> postChecker.ensure(postId, actorId)).doesNotThrowAnyException();

        verify(1, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId))
                .withHeader("X-Auth-User-Id", equalTo(actorId.toString()))
                .withHeader("X-Correlation-Id", equalTo(correlationId)));
    }

    @Test
    void forbiddenPostIsReactionConflictAndDoesNotTripCircuit() {
        UUID postId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId)).willReturn(aResponse().withStatus(403)));

        assertThatThrownBy(() -> postChecker.ensure(postId, UUID.randomUUID()))
                .isInstanceOf(ReactionConflictException.class)
                .hasMessage("Post is not reactable");

        verify(1, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId)));
        assertThat(postCircuit.getMetrics().getNumberOfFailedCalls()).isZero();
    }

    @Test
    void activeCommentPropagatesInternalCredentialAndCorrelationHeader() {
        UUID commentId = UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();
        stubFor(get(urlEqualTo("/internal/v1/comments/" + commentId + "/availability"))
                .willReturn(okJson(commentAvailability(commentId, true))));
        MDC.put("correlationId", correlationId);
        UUID actorId = UUID.randomUUID();

        assertThatCode(() -> commentChecker.ensure(commentId, actorId)).doesNotThrowAnyException();

        verify(1, getRequestedFor(urlEqualTo("/internal/v1/comments/" + commentId + "/availability"))
                .withHeader("X-Internal-Service-Token", equalTo("contract-test-token"))
                .withHeader("X-Auth-User-Id", equalTo(actorId.toString()))
                .withHeader("X-Correlation-Id", equalTo(correlationId)));
    }

    @Test
    void unavailableCommentIsNotRetriedOrCountedAsDependencyFailure() {
        UUID commentId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/internal/v1/comments/" + commentId + "/availability"))
                .willReturn(okJson(commentAvailability(commentId, false))));

        assertThatThrownBy(() -> commentChecker.ensure(commentId, UUID.randomUUID()))
                .isInstanceOf(TargetNotFoundException.class);

        verify(1, getRequestedFor(urlEqualTo("/internal/v1/comments/" + commentId + "/availability")));
        assertThat(commentCircuit.getMetrics().getNumberOfFailedCalls()).isZero();
    }

    @Test
    void rejectedInternalCredentialIsNotRetriedOrCountedAsOutage() {
        UUID commentId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/internal/v1/comments/" + commentId + "/availability"))
                .willReturn(aResponse().withStatus(401)));

        assertThatThrownBy(() -> commentChecker.ensure(commentId, UUID.randomUUID()))
                .isInstanceOf(DependencyRejectedException.class)
                .hasMessageContaining("HTTP 401");

        verify(1, getRequestedFor(urlEqualTo("/internal/v1/comments/" + commentId + "/availability")));
        assertThat(commentCircuit.getMetrics().getNumberOfFailedCalls()).isZero();
    }

    @Test
    void commentServiceFailureIsRetriedAndFailsClosed() {
        UUID commentId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/internal/v1/comments/" + commentId + "/availability"))
                .willReturn(serverError()));

        assertThatThrownBy(() -> commentChecker.ensure(commentId, UUID.randomUUID()))
                .isInstanceOf(DependencyUnavailableException.class);

        verify(2, getRequestedFor(urlEqualTo("/internal/v1/comments/" + commentId + "/availability")));
        assertThat(commentCircuit.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    private String publicPostResponse(UUID postId) {
        UUID ownerId = UUID.randomUUID();
        return """
                {"code":1000,"message":"ok","status":200,
                 "data":{"id":"%s","userId":"%s","status":"PUBLIC"}}
                """.formatted(postId, ownerId);
    }

    private String commentAvailability(UUID commentId, boolean available) {
        UUID ownerId = UUID.randomUUID();
        return """
                {"targetId":"%s","ownerId":"%s","available":%s,"reason":"%s"}
                """.formatted(commentId, ownerId, available, available ? "ACTIVE" : "DELETED");
    }
}
