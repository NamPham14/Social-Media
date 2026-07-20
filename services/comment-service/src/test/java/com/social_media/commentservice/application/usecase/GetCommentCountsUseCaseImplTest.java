package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.domain.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetCommentCountsUseCaseImplTest {

    private CommentRepository repository;
    private GetCommentCountsUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(CommentRepository.class);
        useCase = new GetCommentCountsUseCaseImpl(repository);
    }

    @Test
    void singleCountReturnsRepositoryValueWithoutRemoteValidation() {
        UUID postId = UUID.randomUUID();
        when(repository.countActiveByPostId(postId)).thenReturn(3L);

        var result = useCase.get(postId);

        assertThat(result.postId()).isEqualTo(postId);
        assertThat(result.commentCount()).isEqualTo(3L);
        verify(repository).countActiveByPostId(postId);
    }

    @Test
    void batchDeduplicatesInInputOrderAndDefaultsMissingCountsToZero() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID missing = UUID.randomUUID();
        when(repository.countActiveByPostIds(List.of(first, second, missing)))
                .thenReturn(Map.of(first, 4L, second, 1L));

        var result = useCase.getBatch(List.of(first, second, first, missing));

        assertThat(result).extracting(response -> response.postId())
                .containsExactly(first, second, missing);
        assertThat(result).extracting(response -> response.commentCount())
                .containsExactly(4L, 1L, 0L);
        verify(repository).countActiveByPostIds(List.of(first, second, missing));
    }
}
