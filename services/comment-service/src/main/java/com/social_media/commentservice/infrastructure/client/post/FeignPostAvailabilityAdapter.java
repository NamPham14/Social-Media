package com.social_media.commentservice.infrastructure.client.post;

import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import com.social_media.commentservice.domain.exception.DependencyUnavailableException;
import com.social_media.commentservice.domain.exception.InvalidCommentException;
import com.social_media.commentservice.domain.exception.TargetNotFoundException;
import com.social_media.common.api.ApiResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeignPostAvailabilityAdapter implements PostAvailabilityPort {
    private final PostClient postClient;

    @Override
    @Retry(name = "postAvailability")
    @CircuitBreaker(name = "postAvailability", fallbackMethod = "unavailable")
    public void ensureCommentable(UUID postId, UUID actorId) {
        try {
            ApiResponse<PostSnapshot> response = postClient.getPost(postId, actorId);
            PostSnapshot post = response == null ? null : response.getData();
            if (post == null || post.id() == null) throw new TargetNotFoundException("Post '" + postId + "' does not exist");
            if (!"PUBLIC".equals(post.status())) throw new InvalidCommentException("Post is not commentable");
        } catch (FeignException.NotFound ex) {
            throw new TargetNotFoundException("Post '" + postId + "' does not exist");
        } catch (FeignException ex) {
            throw new DependencyUnavailableException("post-service");
        }
    }

    @SuppressWarnings("unused")
    private void unavailable(UUID postId, UUID actorId, Throwable failure) {
        if (failure instanceof TargetNotFoundException targetNotFound) throw targetNotFound;
        if (failure instanceof InvalidCommentException invalid) throw invalid;
        throw new DependencyUnavailableException("post-service");
    }
}
