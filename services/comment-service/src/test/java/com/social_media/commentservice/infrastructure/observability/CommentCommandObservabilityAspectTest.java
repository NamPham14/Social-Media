package com.social_media.commentservice.infrastructure.observability;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.usecase.CreateCommentUseCase;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentCommandObservabilityAspectTest {

    private SimpleMeterRegistry meterRegistry;
    private CommentCommandObservabilityAspect aspect;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        aspect = new CommentCommandObservabilityAspect(meterRegistry);
        MDC.put("correlationId", UUID.randomUUID().toString());
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        meterRegistry.close();
    }

    @Test
    void successfulCreateRecordsLatencyWithoutActorOrContentTags() throws Throwable {
        UUID postId = UUID.randomUUID();
        CommentResponse response = CommentResponse.builder()
                .id(UUID.randomUUID())
                .postId(postId)
                .build();
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{
                new CreateCommentCommand(postId, UUID.randomUUID(), null, "sensitive content")
        });
        when(joinPoint.proceed()).thenReturn(response);

        assertThat(aspect.observeCreate(joinPoint)).isSameAs(response);

        var timer = meterRegistry.get(CommentCommandObservabilityAspect.DURATION_METRIC)
                .tags("operation", "create", "outcome", "success")
                .timer();
        assertThat(timer.count()).isEqualTo(1);
        assertThat(timer.getId().getTags()).extracting(tag -> tag.getKey())
                .containsExactlyInAnyOrder("operation", "outcome");
    }

    @Test
    void failedDeleteRecordsLatencyAndErrorCount() throws Throwable {
        UUID commentId = UUID.randomUUID();
        IllegalStateException failure = new IllegalStateException("database unavailable");
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{commentId, UUID.randomUUID()});
        when(joinPoint.proceed()).thenThrow(failure);

        assertThatThrownBy(() -> aspect.observeDelete(joinPoint)).isSameAs(failure);

        assertThat(meterRegistry.get(CommentCommandObservabilityAspect.DURATION_METRIC)
                .tags("operation", "delete", "outcome", "error").timer().count()).isEqualTo(1);
        assertThat(meterRegistry.get(CommentCommandObservabilityAspect.ERROR_METRIC)
                .tag("operation", "delete").counter().count()).isEqualTo(1);
    }

    @Test
    void pointcutInterceptsTheApplicationPortImplementation() {
        UUID postId = UUID.randomUUID();
        CommentResponse response = CommentResponse.builder().id(UUID.randomUUID()).postId(postId).build();
        AspectJProxyFactory factory = new AspectJProxyFactory(new StubCreateCommentUseCase(response));
        factory.addAspect(aspect);
        CreateCommentUseCase proxy = factory.getProxy();

        assertThat(proxy.execute(new CreateCommentCommand(
                postId, UUID.randomUUID(), null, "content"))).isSameAs(response);

        assertThat(meterRegistry.get(CommentCommandObservabilityAspect.DURATION_METRIC)
                .tags("operation", "create", "outcome", "success").timer().count()).isEqualTo(1);
    }

    private static final class StubCreateCommentUseCase implements CreateCommentUseCase {
        private final CommentResponse response;

        private StubCreateCommentUseCase(CommentResponse response) {
            this.response = response;
        }

        @Override
        public CommentResponse execute(CreateCommentCommand command) {
            return response;
        }
    }
}
