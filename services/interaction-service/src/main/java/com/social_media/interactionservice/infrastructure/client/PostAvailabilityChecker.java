package com.social_media.interactionservice.infrastructure.client;

import com.social_media.common.api.ApiResponse;
import com.social_media.interactionservice.domain.exception.DependencyUnavailableException;
import com.social_media.interactionservice.domain.exception.ReactionConflictException;
import com.social_media.interactionservice.domain.exception.TargetNotFoundException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostAvailabilityChecker {
    private final PostClient client;

    @Retry(name = "postAvailability")
    @CircuitBreaker(name = "postAvailability", fallbackMethod = "unavailable")
    public void ensure(UUID id, UUID actorId) {
        try {
            ApiResponse<PostClient.PostSnapshot> response = client.getPost(id, actorId);
            PostClient.PostSnapshot post = response == null ? null : response.getData();
            if (post == null) throw new TargetNotFoundException("Post '" + id + "' does not exist");
            if (!"PUBLIC".equals(post.status())) throw new ReactionConflictException("Post is not reactable");
        } catch (FeignException.NotFound ex) {
            throw new TargetNotFoundException("Post '" + id + "' does not exist");
        } catch (FeignException ex) {
            throw new DependencyUnavailableException("post-service");
        }
    }

    @SuppressWarnings("unused")
    private void unavailable(UUID id, UUID actorId, Throwable failure) {
        if (failure instanceof TargetNotFoundException notFound) throw notFound;
        if (failure instanceof ReactionConflictException conflict) throw conflict;
        throw new DependencyUnavailableException("post-service");
    }
}
