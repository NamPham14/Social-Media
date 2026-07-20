package com.social_media.interactionservice.infrastructure.client;

import com.social_media.interactionservice.domain.exception.DependencyRejectedException;
import com.social_media.interactionservice.domain.exception.DependencyUnavailableException;
import com.social_media.interactionservice.domain.exception.TargetNotFoundException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentAvailabilityChecker {
    private final CommentClient client;

    @Value("${internal.service-token:}")
    private String serviceToken;

    @Retry(name = "commentAvailability")
    @CircuitBreaker(name = "commentAvailability", fallbackMethod = "unavailable")
    public UUID ensure(UUID id) {
        try {
            CommentClient.Availability availability = client.getAvailability(id, serviceToken);
            if (availability == null || availability.ownerId() == null || !availability.available())
                throw new TargetNotFoundException("Comment '" + id + "' is unavailable");
            return availability.ownerId();
        } catch (FeignException.NotFound ex) {
            throw new TargetNotFoundException("Comment '" + id + "' does not exist");
        } catch (FeignException.FeignClientException ex) {
            throw new DependencyRejectedException("comment-service", ex.status());
        } catch (FeignException ex) {
            throw new DependencyUnavailableException("comment-service");
        }
    }

    @SuppressWarnings("unused")
    private UUID unavailable(UUID id, Throwable failure) {
        if (failure instanceof TargetNotFoundException notFound) throw notFound;
        if (failure instanceof DependencyRejectedException rejected) throw rejected;
        throw new DependencyUnavailableException("comment-service");
    }
}
