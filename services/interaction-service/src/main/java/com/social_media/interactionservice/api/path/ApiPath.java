package com.social_media.interactionservice.api.path;

public final class ApiPath {
    public static final String BASE = "/api/v1";
    public static final String INTERACTIONS = "/interactions";
    public static final String INTERACTION = "/interactions/{targetType}/{targetId}/{reactionType}";
    public static final String MY_INTERACTIONS = "/interactions/me/{targetType}/{targetId}";
    public static final String COUNTER = "/interactions/counters/{targetType}/{targetId}";
    public static final String COUNTERS_BATCH = "/interactions/counters/batch";

    private ApiPath() {
    }
}
