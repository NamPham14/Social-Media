package com.social_media.commentservice.api.controller;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.api.path.ApiPath;
import com.social_media.commentservice.application.usecase.GetCommentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.beans.factory.annotation.Value;
import com.social_media.commentservice.domain.exception.InternalAccessDeniedException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InternalCommentController {
    private final GetCommentUseCase getCommentUseCase;

    @Value("${internal.service-token:}")
    private String serviceToken;

    @GetMapping(ApiPath.INTERNAL_COMMENT_AVAILABILITY)
    public AvailabilityResponse availability(
            @PathVariable UUID commentId,
            @RequestHeader("X-Internal-Service-Token") String suppliedToken) {
        if (serviceToken.isBlank() || !constantTimeEquals(serviceToken, suppliedToken)) {
            throw new InternalAccessDeniedException();
        }
        CommentResponse comment = getCommentUseCase.execute(commentId);
        return new AvailabilityResponse(commentId, !comment.isDeleted(),
                comment.isDeleted() ? "COMMENT_DELETED" : null);
    }

    public record AvailabilityResponse(UUID targetId, boolean available, String reason) { }

    private boolean constantTimeEquals(String expected, String supplied) {
        return java.security.MessageDigest.isEqual(
                expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
