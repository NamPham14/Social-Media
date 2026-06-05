package com.social_media.followerservice.api;

public final class ApiPath {
    
    private ApiPath() {
    }

    public static final String BASE_API = "/api/v1";
    
    public static final String FOLLOWER_API = "/users/{id}/followers";
    public static final String FOLLOWING_API = "/users/{id}/following";
    public static final String FEED_API = "/feeds";
}
