package com.social_media.commentservice.api.controller;

import com.social_media.commentservice.api.path.ApiPath;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import com.social_media.commentservice.domain.exception.CommentNotFoundException;
import com.social_media.commentservice.domain.repository.CommentRepository;
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
    private final CommentRepository commentRepository;
    private final PostAvailabilityPort postAvailabilityPort;

    @Value("${internal.service-token:}")
    private String serviceToken;

    @GetMapping(ApiPath.INTERNAL_COMMENT_AVAILABILITY)
    public AvailabilityResponse availability(
            @PathVariable("commentId") UUID commentId,
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @RequestHeader("X-Internal-Service-Token") String suppliedToken) {
        if (serviceToken.isBlank() || !constantTimeEquals(serviceToken, suppliedToken)) {
            throw new InternalAccessDeniedException();
        }
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.isDeleted()) {
            postAvailabilityPort.getCommentable(comment.getPostId(), actorId);
        }
        return new AvailabilityResponse(commentId, comment.getPostId(), comment.getUserId(), !comment.isDeleted(),
                comment.isDeleted() ? "COMMENT_DELETED" : null);
    }

    public record AvailabilityResponse(UUID targetId, UUID postId, UUID ownerId,
                                       boolean available, String reason) { }

    private boolean constantTimeEquals(String expected, String supplied) {
        return java.security.MessageDigest.isEqual(
                expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
