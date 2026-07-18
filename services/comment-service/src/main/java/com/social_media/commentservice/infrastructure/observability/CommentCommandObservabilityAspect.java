package com.social_media.commentservice.infrastructure.observability;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.application.command.CreateCommentCommand;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CommentCommandObservabilityAspect {

    static final String DURATION_METRIC = "social.comment.command.duration";
    static final String ERROR_METRIC = "social.comment.command.errors";

    private final MeterRegistry meterRegistry;

    @Around("execution(* com.social_media.commentservice.application.usecase.CreateCommentUseCase+.execute(..))")
    public Object observeCreate(ProceedingJoinPoint joinPoint) throws Throwable {
        CreateCommentCommand command = (CreateCommentCommand) joinPoint.getArgs()[0];
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            CommentResponse response = (CommentResponse) joinPoint.proceed();
            long duration = sample.stop(timer("create", "success"));
            log.info("service=comment-service correlationId={} operation=create aggregateId={} targetId={} outcome=success durationMs={}",
                    correlationId(), response.getId(), command.postId(), millis(duration));
            return response;
        } catch (Throwable failure) {
            recordFailure("create", command.postId(), sample, failure);
            throw failure;
        }
    }

    @Around("execution(* com.social_media.commentservice.application.usecase.DeleteCommentUseCase+.execute(..))")
    public Object observeDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        UUID commentId = (UUID) joinPoint.getArgs()[0];
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Object result = joinPoint.proceed();
            long duration = sample.stop(timer("delete", "success"));
            log.info("service=comment-service correlationId={} operation=delete aggregateId={} outcome=success durationMs={}",
                    correlationId(), commentId, millis(duration));
            return result;
        } catch (Throwable failure) {
            recordFailure("delete", commentId, sample, failure);
            throw failure;
        }
    }

    private void recordFailure(String operation, UUID aggregateId, Timer.Sample sample, Throwable failure) {
        long duration = sample.stop(timer(operation, "error"));
        meterRegistry.counter(ERROR_METRIC, "operation", operation).increment();
        log.warn("service=comment-service correlationId={} operation={} aggregateId={} outcome=error errorType={} durationMs={}",
                correlationId(), operation, aggregateId, failure.getClass().getSimpleName(), millis(duration));
    }

    private Timer timer(String operation, String outcome) {
        return Timer.builder(DURATION_METRIC)
                .description("Comment command latency")
                .tag("operation", operation)
                .tag("outcome", outcome)
                .register(meterRegistry);
    }

    private long millis(long nanoseconds) {
        return TimeUnit.NANOSECONDS.toMillis(nanoseconds);
    }

    private String correlationId() {
        String value = MDC.get("correlationId");
        return value == null ? "missing" : value;
    }
}
