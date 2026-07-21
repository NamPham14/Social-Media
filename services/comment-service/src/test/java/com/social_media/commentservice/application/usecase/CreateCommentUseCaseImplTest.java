package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.event.CommentNotificationEvent;
import com.social_media.commentservice.application.port.out.CommentEventOutbox;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import com.social_media.commentservice.domain.exception.InvalidCommentException;
import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import com.social_media.commentservice.infrastructure.client.profile.ProfileClient;
//import com.social_media.commentservice.infrastructure.client.profile.dto.ProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateCommentUseCaseImplTest {
    private CommentRepository repository;
    private PostAvailabilityPort availability;
    private CommentEventOutbox outbox;
    private ProfileClient profileClient;
    private CreateCommentUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(CommentRepository.class);
        availability = mock(PostAvailabilityPort.class);
        outbox = mock(CommentEventOutbox.class);
        profileClient = mock(ProfileClient.class);
        useCase = new CreateCommentUseCaseImpl(repository, availability, outbox, profileClient);
        
//        when(profileClient.getProfile(any())).thenReturn(
//                new ProfileResponse(UUID.randomUUID(), "Test User", "test-avatar.jpg", "test_bio")
//        );
        
        when(availability.getCommentable(any(), any())).thenAnswer(invocation ->
                new PostAvailabilityPort.AvailablePost(invocation.getArgument(0), UUID.randomUUID()));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void validatesPostAndUsesAuthenticatedActor() {
        UUID postId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        Comment saved = mock(Comment.class);
        when(saved.getId()).thenReturn(commentId);
        when(saved.getPostId()).thenReturn(postId);
        when(saved.getUserId()).thenReturn(actorId);
        when(repository.save(any())).thenReturn(saved);
        when(availability.getCommentable(postId, actorId))
                .thenReturn(new PostAvailabilityPort.AvailablePost(postId, ownerId));

        useCase.execute(new CreateCommentCommand(postId, actorId, null, "hello"));
        verify(availability).getCommentable(postId, actorId);
        verify(repository).save(argThat(c -> c.getUserId().equals(actorId)));
        verify(outbox).append(argThat((CommentNotificationEvent event) ->
                event.eventType().equals(CommentNotificationEvent.COMMENT_CREATED)
                        && event.commentId().equals(commentId)
                        && event.recipientId().equals(ownerId)));
    }

    @Test
    void selfCommentDoesNotCreateNotificationEvent() {
        UUID postId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        when(availability.getCommentable(postId, actorId))
                .thenReturn(new PostAvailabilityPort.AvailablePost(postId, actorId));

        useCase.execute(new CreateCommentCommand(postId, actorId, null, "hello"));

        verify(outbox, never()).append(any(CommentNotificationEvent.class));
    }

    @Test
    void replyNotifiesParentOwnerInsteadOfPostOwner() {
        UUID postId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        UUID postOwnerId = UUID.randomUUID();
        UUID parentOwnerId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        UUID replyId = UUID.randomUUID();
        Comment parent = mock(Comment.class);
        when(parent.getPostId()).thenReturn(postId);
        when(parent.getParentId()).thenReturn(null);
        when(parent.getUserId()).thenReturn(parentOwnerId);
        Comment saved = mock(Comment.class);
        when(saved.getId()).thenReturn(replyId);
        when(saved.getPostId()).thenReturn(postId);
        when(saved.getParentId()).thenReturn(parentId);
        when(saved.getUserId()).thenReturn(actorId);
        when(repository.findById(parentId)).thenReturn(Optional.of(parent));
        when(repository.save(any())).thenReturn(saved);
        when(availability.getCommentable(postId, actorId))
                .thenReturn(new PostAvailabilityPort.AvailablePost(postId, postOwnerId));

        useCase.execute(new CreateCommentCommand(postId, actorId, parentId, "reply"));

        verify(outbox).append(argThat((CommentNotificationEvent event) ->
                event.eventType().equals(CommentNotificationEvent.COMMENT_REPLIED)
                        && event.parentCommentId().equals(parentId)
                        && event.recipientId().equals(parentOwnerId)));
    }

    @Test
    void rejectsParentFromAnotherPost() {
        UUID postId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        Comment parent = Comment.create(UUID.randomUUID(), UUID.randomUUID(), "Author", "Avatar", null, "parent");
        when(repository.findById(parentId)).thenReturn(Optional.of(parent));
        assertThatThrownBy(() -> useCase.execute(new CreateCommentCommand(
                postId, UUID.randomUUID(), parentId, "reply")))
                .isInstanceOf(InvalidCommentException.class)
                .hasMessageContaining("another post");
    }

    @Test
    void rejectsReplyToReply() {
        UUID postId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        Comment reply = Comment.create(postId, UUID.randomUUID(), "Author", "Avatar", UUID.randomUUID(), "reply");
        when(repository.findById(parentId)).thenReturn(Optional.of(reply));
        assertThatThrownBy(() -> useCase.execute(new CreateCommentCommand(
                postId, UUID.randomUUID(), parentId, "nested")))
                .isInstanceOf(InvalidCommentException.class)
                .hasMessageContaining("one reply level");
    }
}
