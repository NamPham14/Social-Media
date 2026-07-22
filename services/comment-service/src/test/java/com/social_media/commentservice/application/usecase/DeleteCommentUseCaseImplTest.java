package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.application.event.CommentDeletedEvent;
import com.social_media.commentservice.application.port.out.CommentEventOutbox;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteCommentUseCaseImplTest {
    @Test
    void authorDeletePersistsAndPublishesCleanupEventAtomically() {
        CommentRepository comments = mock(CommentRepository.class);
        PostAvailabilityPort posts = mock(PostAvailabilityPort.class);
        CommentEventOutbox outbox = mock(CommentEventOutbox.class);
        DeleteCommentUseCaseImpl useCase = new DeleteCommentUseCaseImpl(comments, posts, outbox);
        Comment comment = mock(Comment.class);
        UUID commentId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        when(comment.getId()).thenReturn(commentId);
        when(comment.getPostId()).thenReturn(postId);
        when(comment.getUserId()).thenReturn(authorId);
        when(comment.softDelete(authorId, null)).thenReturn(true);
        when(comments.findById(commentId)).thenReturn(Optional.of(comment));

        useCase.execute(commentId, authorId);

        verifyNoInteractions(posts);
        verify(comments).save(comment);
        verify(outbox).append(argThat((CommentDeletedEvent event) ->
                event.commentId().equals(commentId) && event.postId().equals(postId)
                        && event.actorId().equals(authorId)));
    }

    @Test
    void postOwnerCanDeleteButDuplicateDeleteDoesNotPublishAgain() {
        CommentRepository comments = mock(CommentRepository.class);
        PostAvailabilityPort posts = mock(PostAvailabilityPort.class);
        CommentEventOutbox outbox = mock(CommentEventOutbox.class);
        DeleteCommentUseCaseImpl useCase = new DeleteCommentUseCaseImpl(comments, posts, outbox);
        Comment comment = mock(Comment.class);
        UUID commentId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID postOwnerId = UUID.randomUUID();
        when(comment.getPostId()).thenReturn(postId);
        when(comment.getUserId()).thenReturn(UUID.randomUUID());
        when(comments.findById(commentId)).thenReturn(Optional.of(comment));
        when(posts.getCommentable(postId, postOwnerId))
                .thenReturn(new PostAvailabilityPort.AvailablePost(postId, postOwnerId));
        when(comment.softDelete(postOwnerId, postOwnerId)).thenReturn(false);

        useCase.execute(commentId, postOwnerId);

        verify(comments, never()).save(any());
        verify(outbox, never()).append(any(CommentDeletedEvent.class));
    }

    @Test
    void postOwnerDeletePersistsAndPublishesCleanupEvent() {
        CommentRepository comments = mock(CommentRepository.class);
        PostAvailabilityPort posts = mock(PostAvailabilityPort.class);
        CommentEventOutbox outbox = mock(CommentEventOutbox.class);
        DeleteCommentUseCaseImpl useCase = new DeleteCommentUseCaseImpl(comments, posts, outbox);
        Comment comment = mock(Comment.class);
        UUID commentId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID postOwnerId = UUID.randomUUID();
        when(comment.getId()).thenReturn(commentId);
        when(comment.getPostId()).thenReturn(postId);
        when(comment.getUserId()).thenReturn(authorId);
        when(comments.findById(commentId)).thenReturn(Optional.of(comment));
        when(posts.getCommentable(postId, postOwnerId))
                .thenReturn(new PostAvailabilityPort.AvailablePost(postId, postOwnerId));
        when(comment.softDelete(postOwnerId, postOwnerId)).thenReturn(true);

        useCase.execute(commentId, postOwnerId);

        verify(comments).save(comment);
        verify(outbox).append(argThat((CommentDeletedEvent event) ->
                event.commentId().equals(commentId) && event.actorId().equals(postOwnerId)));
    }
}
