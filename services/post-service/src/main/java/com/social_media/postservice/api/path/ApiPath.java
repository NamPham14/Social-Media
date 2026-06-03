package com.social_media.postservice.api.path;

public final class ApiPath {
    public static final String BASE = "/api/v1";
    public static final String POSTS = "/posts";


    public static final String POST_BY_ID = "/posts/{postId}";


    public static final String POSTS_BY_AUTHOR = "/posts/user/{userId}";

    public static final String POSTS_SEARCH = "/posts/search";

    public static final String SEARCHING = BASE + "/posts/{keyword}";

    public static final String POST_SUBMIT = "/posts/submit";
    public static final String POST_MOVE_TO_DRAFT = "/posts/draft";
    public static final String POST_APPROVE = "/posts/approve";
    public static final String POST_REJECT = "/posts/reject";
    public static final String POST_DELETE = "/posts/delete";
    public static final String POST_UPDATE = "/posts/update";
}
