package com.social_media.chatservice.utils.path;

public final class ApiPath {
    private ApiPath() {}

    public static final String BASE = "/api/v1/chat";

    public static final String CONVERSATIONS = BASE + "/conversations";
    public static final String CONVERSATION_BY_ID = BASE + "/conversations/{conversationId}";
    public static final String CONVERSATION_MESSAGES = BASE + "/conversations/{conversationId}/messages";
    public static final String MARK_READ = BASE + "/conversations/{conversationId}/read";
    public static final String MESSAGE_BY_ID = BASE + "/messages/{messageId}";
}
