package com.social_media.interactionservice.infrastructure.observability;

import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
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
public class InteractionCommandObservabilityAspect {

    static final String DURATION_METRIC = "social.interaction.command.duration";
    static final String ERROR_METRIC = "social.interaction.command.errors";
    static final String DUPLICATE_METRIC = "social.interaction.duplicates";
    static final String COUNTER_FAILURE_METRIC = "social.interaction.counter.update.failures";

    private final MeterRegistry meterRegistry;

    @Around("execution(* com.social_media.interactionservice.application.usecase.CreateInteractionUseCase+.execute(..))")
    public Object observeCreate(ProceedingJoinPoint joinPoint) throws Throwable {
        CreateInteractionCommand command = (CreateInteractionCommand) joinPoint.getArgs()[0];
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            InteractionResponse response = (InteractionResponse) joinPoint.proceed();
            String outcome = response.isDuplicateIgnored() ? "duplicate" : "created";
            long duration = sample.stop(timer("create", "success"));
            if (response.isDuplicateIgnored()) {
                meterRegistry.counter(DUPLICATE_METRIC,
                        "target_type", command.targetType().name(),
                        "reaction_type", command.reactionType().name()).increment();
            }
            log.info("service=interaction-service correlationId={} operation=create aggregateId={} targetType={} reactionType={} outcome={} durationMs={}",
                    correlationId(), command.targetId(), command.targetType(), command.reactionType(), outcome,
                    millis(duration));
            return response;
        } catch (Throwable failure) {
            recordCommandFailure("create", command.targetId(), sample, failure);
            throw failure;
        }
    }

    @Around("execution(* com.social_media.interactionservice.application.usecase.RemoveInteractionUseCase+.execute(..))")
    public Object observeRemove(ProceedingJoinPoint joinPoint) throws Throwable {
        TargetType targetType = (TargetType) joinPoint.getArgs()[1];
        UUID targetId = (UUID) joinPoint.getArgs()[2];
        ReactionType reactionType = (ReactionType) joinPoint.getArgs()[3];
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            boolean removed = (boolean) joinPoint.proceed();
            long duration = sample.stop(timer("remove", "success"));
            log.info("service=interaction-service correlationId={} operation=remove aggregateId={} targetType={} reactionType={} outcome={} durationMs={}",
                    correlationId(), targetId, targetType, reactionType, removed ? "removed" : "no_op",
                    millis(duration));
            return removed;
        } catch (Throwable failure) {
            recordCommandFailure("remove", targetId, sample, failure);
            throw failure;
        }
    }

    @Around("execution(* com.social_media.interactionservice.domain.repository.InteractionCounterRepository+.increment(..)) || "
            + "execution(* com.social_media.interactionservice.domain.repository.InteractionCounterRepository+.decrement(..))")
    public Object observeCounterUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        String operation = joinPoint.getSignature().getName();
        TargetType targetType = (TargetType) joinPoint.getArgs()[0];
        UUID targetId = (UUID) joinPoint.getArgs()[1];
        ReactionType reactionType = (ReactionType) joinPoint.getArgs()[2];
        try {
            return joinPoint.proceed();
        } catch (Throwable failure) {
            meterRegistry.counter(COUNTER_FAILURE_METRIC, "operation", operation).increment();
            log.error("service=interaction-service correlationId={} operation=counter_{} aggregateId={} targetType={} reactionType={} outcome=error errorType={}",
                    correlationId(), operation, targetId, targetType, reactionType,
                    failure.getClass().getSimpleName(), failure);
            throw failure;
        }
    }

    private void recordCommandFailure(String operation, UUID aggregateId, Timer.Sample sample, Throwable failure) {
        long duration = sample.stop(timer(operation, "error"));
        meterRegistry.counter(ERROR_METRIC, "operation", operation).increment();
        log.warn("service=interaction-service correlationId={} operation={} aggregateId={} outcome=error errorType={} durationMs={}",
                correlationId(), operation, aggregateId, failure.getClass().getSimpleName(), millis(duration));
    }

    private Timer timer(String operation, String outcome) {
        return Timer.builder(DURATION_METRIC)
                .description("Interaction command latency")
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
