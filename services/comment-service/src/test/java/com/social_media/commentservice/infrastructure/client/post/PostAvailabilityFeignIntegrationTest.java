package com.social_media.commentservice.infrastructure.client.post;

import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import com.social_media.commentservice.domain.exception.DependencyRejectedException;
import com.social_media.commentservice.domain.exception.DependencyUnavailableException;
import com.social_media.commentservice.domain.exception.InvalidCommentException;
import com.social_media.commentservice.domain.exception.TargetNotFoundException;
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

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        classes = PostAvailabilityFeignIntegrationTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.cloud.discovery.enabled=false",
                "eureka.client.enabled=false",
                "spring.cloud.openfeign.client.config.post-service.url=http://localhost:${wiremock.server.port}",
                "spring.cloud.openfeign.client.config.post-service.connectTimeout=100",
                "spring.cloud.openfeign.client.config.post-service.readTimeout=500",
                "resilience4j.retry.instances.postAvailability.max-attempts=2",
                "resilience4j.retry.instances.postAvailability.wait-duration=1ms",
                "resilience4j.retry.instances.postAvailability.ignore-exceptions[0]=com.social_media.commentservice.domain.exception.TargetNotFoundException",
                "resilience4j.retry.instances.postAvailability.ignore-exceptions[1]=com.social_media.commentservice.domain.exception.InvalidCommentException",
                "resilience4j.retry.instances.postAvailability.ignore-exceptions[2]=com.social_media.commentservice.domain.exception.DependencyRejectedException",
                "resilience4j.circuitbreaker.instances.postAvailability.sliding-window-size=2",
                "resilience4j.circuitbreaker.instances.postAvailability.minimum-number-of-calls=2",
                "resilience4j.circuitbreaker.instances.postAvailability.failure-rate-threshold=50",
                "resilience4j.circuitbreaker.instances.postAvailability.wait-duration-in-open-state=1h",
                "resilience4j.circuitbreaker.instances.postAvailability.permitted-number-of-calls-in-half-open-state=1",
                "resilience4j.circuitbreaker.instances.postAvailability.ignore-exceptions[0]=com.social_media.commentservice.domain.exception.TargetNotFoundException",
                "resilience4j.circuitbreaker.instances.postAvailability.ignore-exceptions[1]=com.social_media.commentservice.domain.exception.InvalidCommentException",
                "resilience4j.circuitbreaker.instances.postAvailability.ignore-exceptions[2]=com.social_media.commentservice.domain.exception.DependencyRejectedException"
        })
@AutoConfigureWireMock(port = 0)
class PostAvailabilityFeignIntegrationTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            FlywayAutoConfiguration.class
    })
    @EnableFeignClients(clients = PostClient.class)
    @Import(FeignPostAvailabilityAdapter.class)
    static class TestApplication {
    }

    private final PostAvailabilityPort availability;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    PostAvailabilityFeignIntegrationTest(
            PostAvailabilityPort availability,
            CircuitBreakerRegistry circuitBreakerRegistry) {
        this.availability = availability;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("postAvailability");
    }

    @BeforeEach
    void resetDependencies() {
        reset();
        circuitBreaker.reset();
    }

    @AfterEach
    void clearCorrelationContext() {
        MDC.clear();
    }

    @Test
    void publicPostPropagatesActorAndCorrelationHeaders() {
        UUID postId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId))
                .willReturn(okJson(publicPostResponse(postId))));
        MDC.put("correlationId", correlationId);

        assertThat(availability.getCommentable(postId, actorId).ownerId()).isNotNull();

        verify(1, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId))
                .withHeader("X-Auth-User-Id", equalTo(actorId.toString()))
                .withHeader("X-Correlation-Id", equalTo(correlationId)));
    }

    @Test
    void missingPostIsNotRetriedOrCountedAsDependencyFailure() {
        UUID postId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId)).willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> availability.getCommentable(postId, UUID.randomUUID()))
                .isInstanceOf(TargetNotFoundException.class);

        verify(1, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId)));
        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isZero();
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void forbiddenPostIsBusinessRejectionAndIsNotRetried() {
        UUID postId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId)).willReturn(aResponse().withStatus(403)));

        assertThatThrownBy(() -> availability.getCommentable(postId, UUID.randomUUID()))
                .isInstanceOf(InvalidCommentException.class)
                .hasMessage("Post is not commentable");

        verify(1, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId)));
        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isZero();
    }

    @Test
    void rejectedClientRequestIsNotRetriedOrCountedAsOutage() {
        UUID postId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId)).willReturn(aResponse().withStatus(401)));

        assertThatThrownBy(() -> availability.getCommentable(postId, UUID.randomUUID()))
                .isInstanceOf(DependencyRejectedException.class)
                .hasMessageContaining("HTTP 401");

        verify(1, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId)));
        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isZero();
    }

    @Test
    void serverFailureIsRetriedWithinTheConfiguredBudget() {
        UUID postId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId)).willReturn(serverError()));

        assertThatThrownBy(() -> availability.getCommentable(postId, UUID.randomUUID()))
                .isInstanceOf(DependencyUnavailableException.class);

        verify(2, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId)));
    }

    @Test
    void readTimeoutFailsClosedAndIsRetriedOnce() {
        UUID postId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId))
                .willReturn(okJson(publicPostResponse(postId)).withFixedDelay(1000)));

        assertThatThrownBy(() -> availability.getCommentable(postId, UUID.randomUUID()))
                .isInstanceOf(DependencyUnavailableException.class);

        verify(2, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId)));
    }

    @Test
    void circuitOpensAfterThresholdAndRecoversThroughHalfOpenProbe() {
        UUID postId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/api/v1/posts/" + postId)).willReturn(serverError()));

        assertThatThrownBy(() -> availability.getCommentable(postId, actorId))
                .isInstanceOf(DependencyUnavailableException.class);
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThatThrownBy(() -> availability.getCommentable(postId, actorId))
                .isInstanceOf(DependencyUnavailableException.class);
        verify(2, getRequestedFor(urlEqualTo("/api/v1/posts/" + postId)));

        stubFor(get(urlEqualTo("/api/v1/posts/" + postId))
                .willReturn(okJson(publicPostResponse(postId))));
        circuitBreaker.transitionToHalfOpenState();

        assertThatCode(() -> availability.getCommentable(postId, actorId)).doesNotThrowAnyException();
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    private String publicPostResponse(UUID postId) {
        UUID ownerId = UUID.randomUUID();
        return """
                {"code":1000,"message":"ok","status":200,
                 "data":{"id":"%s","userId":"%s","status":"PUBLIC"}}
                """.formatted(postId, ownerId);
    }
}
