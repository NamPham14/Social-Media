package com.social_media.postservice.api.path;

public final class ApiPath {
    public static final String BASE = "/api/v1";
    public static final String POSTS = "/posts";

    public static final String POST_BY_ID = "/posts/{postId}";

    public static final String POSTS_BY_AUTHOR = "/posts/user/{userId}";

    public static final String POSTS_SEARCH = "/posts/search";

    public static final String SEARCHING = BASE + "/posts/{keyword}";

    public static final String POST_DELETE = "/posts/delete";
    public static final String POST_UPDATE = "/posts/update";

    public static final String POST_REPORT = "/posts/{postId}/report";
    public static final String REPORTS = "/reports";
    public static final String REPORT_DISMISS = "/reports/{reportId}/dismiss";
    public static final String REPORT_REMOVE = "/reports/{reportId}/remove";

    public static final String BOOKMARKS = "/bookmarks";
    public static final String BOOKMARK = "/bookmarks/{postId}";

    public static final String POSTS_BY_AUTHORS = "/posts/by-authors";
}
