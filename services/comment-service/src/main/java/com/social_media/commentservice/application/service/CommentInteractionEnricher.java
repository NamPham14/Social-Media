package com.social_media.commentservice.application.service;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.application.port.out.InteractionSummaryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentInteractionEnricher {
    private final InteractionSummaryPort interactionSummaryPort;

    public List<CommentResponse> enrich(List<CommentResponse> comments, UUID actorId) {
        var summaries = interactionSummaryPort.getCommentSummaries(actorId,
                comments.stream().map(CommentResponse::getId).toList());
        return comments.stream().map(comment -> {
            var summary = summaries.get(comment.getId());
            return summary == null ? comment : comment.toBuilder()
                    .reactionCount(summary.reactionCount())
                    .likedByMe(summary.likedByMe())
                    .build();
        }).toList();
    }

    public CommentResponse enrich(CommentResponse comment, UUID actorId) {
        return enrich(List.of(comment), actorId).getFirst();
    }
}
