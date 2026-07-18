package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.application.event.PostCommentsDeletedEvent;
import com.social_media.commentservice.application.event.PostDeletedEvent;
import com.social_media.commentservice.application.port.out.CommentDeletionOutbox;
import com.social_media.commentservice.domain.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCommentsByPostUseCaseImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentDeletionOutbox outbox;
    @InjectMocks
    private DeleteCommentsByPostUseCaseImpl useCase;

    @Test
    void softDeletesCommentsAndAppendsTheirIdsToTheOutbox() {
        UUID postId = UUID.randomUUID();
        List<UUID> commentIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PostDeletedEvent event = new PostDeletedEvent("event-1", postId, UUID.randomUUID());
        when(commentRepository.findActiveIdsByPostId(postId)).thenReturn(commentIds);
        when(commentRepository.softDeleteAllByPostId(postId)).thenReturn(2);

        assertThat(useCase.execute(event)).isEqualTo(2);

        ArgumentCaptor<PostCommentsDeletedEvent> captor = ArgumentCaptor.forClass(PostCommentsDeletedEvent.class);
        verify(outbox).append(captor.capture());
        assertThatCode(() -> UUID.fromString(captor.getValue().id())).doesNotThrowAnyException();
        assertThat(captor.getValue().postId()).isEqualTo(postId);
        assertThat(captor.getValue().commentIds()).containsExactlyElementsOf(commentIds);
    }

    @Test
    void duplicateEventIsANoOpWhenCommentsAreAlreadyDeleted() {
        UUID postId = UUID.randomUUID();
        PostDeletedEvent event = new PostDeletedEvent("event-1", postId, UUID.randomUUID());
        when(commentRepository.findActiveIdsByPostId(postId)).thenReturn(List.of());
        when(commentRepository.softDeleteAllByPostId(postId)).thenReturn(0);

        assertThat(useCase.execute(event)).isZero();

        verify(outbox, never()).append(org.mockito.ArgumentMatchers.any());
    }
}
