package com.social_media.commentservice.api.path;

public final class ApiPath {
    public static final String BASE = "/api/v1";
    public static final String COMMENTS = "/comments";
    public static final String COMMENT_BY_ID = "/comments/{commentId}";
    public static final String COMMENTS_BY_POST = "/posts/{postId}/comments";

    private ApiPath() {
    }
}
