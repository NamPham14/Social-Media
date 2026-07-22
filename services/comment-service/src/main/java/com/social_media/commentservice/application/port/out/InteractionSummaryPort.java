package com.social_media.commentservice.application.port.out;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface InteractionSummaryPort {
    Map<UUID, Summary> getCommentSummaries(UUID actorId, Collection<UUID> commentIds);

    record Summary(int reactionCount, boolean likedByMe) { }
}
