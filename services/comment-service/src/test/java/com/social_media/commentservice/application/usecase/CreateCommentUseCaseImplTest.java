package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import com.social_media.commentservice.domain.exception.InvalidCommentException;
import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateCommentUseCaseImplTest {
    private CommentRepository repository;
    private PostAvailabilityPort availability;
    private CreateCommentUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(CommentRepository.class);
        availability = mock(PostAvailabilityPort.class);
        useCase = new CreateCommentUseCaseImpl(repository, availability);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void validatesPostAndUsesAuthenticatedActor() {
        UUID postId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        useCase.execute(new CreateCommentCommand(postId, actorId, null, "hello"));
        verify(availability).ensureCommentable(postId, actorId);
        verify(repository).save(argThat(c -> c.getUserId().equals(actorId)));
    }

    @Test
    void rejectsParentFromAnotherPost() {
        UUID postId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        Comment parent = Comment.create(UUID.randomUUID(), UUID.randomUUID(), null, "parent");
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
        Comment reply = Comment.create(postId, UUID.randomUUID(), UUID.randomUUID(), "reply");
        when(repository.findById(parentId)).thenReturn(Optional.of(reply));
        assertThatThrownBy(() -> useCase.execute(new CreateCommentCommand(
                postId, UUID.randomUUID(), parentId, "nested")))
                .isInstanceOf(InvalidCommentException.class)
                .hasMessageContaining("one reply level");
    }
}
