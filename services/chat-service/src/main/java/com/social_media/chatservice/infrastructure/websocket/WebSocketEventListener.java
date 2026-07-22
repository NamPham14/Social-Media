package com.social_media.chatservice.infrastructure.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = extractUserId(accessor);
        log.info("User connected: {} (session: {})", userId, accessor.getSessionId());
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = extractUserId(accessor);
        log.info("User disconnected: {} (session: {})", userId, accessor.getSessionId());
    }

    private String extractUserId(StompHeaderAccessor accessor) {
        if (accessor.getUser() != null) {
            return accessor.getUser().getName();
        }
        return null;
    }
}
