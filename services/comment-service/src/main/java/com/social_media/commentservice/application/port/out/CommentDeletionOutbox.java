package com.social_media.commentservice.application.port.out;

import com.social_media.commentservice.application.event.PostCommentsDeletedEvent;

public interface CommentDeletionOutbox {
    void append(PostCommentsDeletedEvent event);
}
