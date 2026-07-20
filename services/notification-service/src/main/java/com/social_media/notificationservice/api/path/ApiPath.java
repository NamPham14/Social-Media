package com.social_media.notificationservice.api.path;

public final class ApiPath {
    private ApiPath() {
    }

    public static final String BASE = "";

    public static final String MY_NOTIFICATIONS = "/me";
    public static final String MY_UNREAD_NOTIFICATIONS = "/me/unread";
    public static final String MY_UNREAD_COUNT = "/me/unread-count";

    public static final String MARK_ALL_AS_READ = "/read-all";
    public static final String MARK_AS_READ = "/{notificationId}/read";
    public static final String MARK_AS_UNREAD = "/{notificationId}/unread";
    public static final String DELETE = "/{notificationId}";
}
