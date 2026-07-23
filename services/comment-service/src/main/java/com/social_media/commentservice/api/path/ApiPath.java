package com.social_media.commentservice.api.path;

public final class ApiPath {
    public static final String BASE = "/api/v1";
    public static final String COMMENTS = "/comments";
    public static final String COMMENT_BY_ID = "/comments/{commentId}";
    public static final String COMMENT_REPLIES = "/comments/{commentId}/replies";
    public static final String COMMENTS_BY_POST = "/posts/{postId}/comments";
    public static final String COMMENT_COUNT_BY_POST = "/posts/{postId}/comments/count";
    public static final String COMMENT_COUNTS_BATCH = "/comments/counts/batch";
    public static final String INTERNAL_COMMENT_AVAILABILITY = "/internal/v1/comments/{commentId}/availability";

    private ApiPath() {
    }
}
