package com.social_media.commentservice.domain.model;

import com.social_media.commentservice.domain.exception.CommentAccessDeniedException;
import com.social_media.commentservice.domain.exception.CommentAlreadyDeletedException;
import com.social_media.commentservice.domain.exception.InvalidCommentException;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class CommentTest {
    private final UUID postId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();

    @Test
    void trimsAndValidatesContent() {
        Comment comment = Comment.create(postId, ownerId, null, "  hello  ");
        assertThat(comment.getContent()).isEqualTo("hello");
        assertThatThrownBy(() -> Comment.create(postId, ownerId, null, " "))
                .isInstanceOf(InvalidCommentException.class);
    }

    @Test
    void onlyOwnerCanEditOrDelete() {
        Comment comment = Comment.create(postId, ownerId, null, "hello");
        assertThatThrownBy(() -> comment.updateContent(UUID.randomUUID(), "changed"))
                .isInstanceOf(CommentAccessDeniedException.class);
        assertThatThrownBy(() -> comment.softDelete(UUID.randomUUID()))
                .isInstanceOf(CommentAccessDeniedException.class);
    }

    @Test
    void deleteIsIdempotentAndDeletedCommentCannotBeEdited() {
        Comment comment = Comment.create(postId, ownerId, null, "hello");
        assertThat(comment.softDelete(ownerId)).isTrue();
        assertThat(comment.softDelete(ownerId)).isFalse();
        assertThatThrownBy(() -> comment.updateContent(ownerId, "changed"))
                .isInstanceOf(CommentAlreadyDeletedException.class);
    }
}
