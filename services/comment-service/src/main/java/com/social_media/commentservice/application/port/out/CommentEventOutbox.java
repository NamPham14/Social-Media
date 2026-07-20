package com.social_media.commentservice.application.port.out;

import com.social_media.commentservice.application.event.CommentNotificationEvent;
import com.social_media.commentservice.application.event.PostCommentsDeletedEvent;

public interface CommentEventOutbox {
    void append(PostCommentsDeletedEvent event);

    void append(CommentNotificationEvent event);
}
