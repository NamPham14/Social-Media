package com.social_media.commentservice.api.controller;

import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.usecase.*;
import com.social_media.commentservice.api.CommentExceptionHandler;
import com.social_media.commentservice.api.dto.CommentCountResponse;
import com.social_media.commentservice.domain.exception.TargetNotFoundException;
import com.social_media.commentservice.infrastructure.web.CorrelationIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.UUID;

class CommentControllerTest {
    private CreateCommentUseCase create;
    private GetCommentCountsUseCase counts;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        create = mock(CreateCommentUseCase.class);
        counts = mock(GetCommentCountsUseCase.class);
        CommentController controller = new CommentController(create, mock(FindCommentsByPostUseCase.class),
                mock(DeleteCommentUseCase.class), mock(UpdateCommentUseCase.class), mock(GetCommentUseCase.class), counts);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new CommentExceptionHandler())
                .addFilters(new CorrelationIdFilter())
                .build();
    }

    @Test
    void missingOrInvalidActorHeaderIsBadRequest() throws Exception {
        String body = "{\"postId\":\"" + UUID.randomUUID() + "\",\"content\":\"hello\"}";
        mvc.perform(post("/api/v1/comments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/comments").header("X-Auth-User-Id", "not-a-uuid")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bodyUserIdCannotSpoofAuthenticatedActor() throws Exception {
        UUID actor = UUID.randomUUID();
        String body = "{\"postId\":\"" + UUID.randomUUID() + "\",\"userId\":\"" + UUID.randomUUID()
                + "\",\"content\":\"hello\"}";
        mvc.perform(post("/api/v1/comments").header("X-Auth-User-Id", actor)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(1000));
        ArgumentCaptor<CreateCommentCommand> captor = ArgumentCaptor.forClass(CreateCommentCommand.class);
        verify(create).execute(captor.capture());
        assertThat(captor.getValue().actorId()).isEqualTo(actor);
    }

    @Test
    void batchCountDelegatesTheBoundedRequestWithoutAuthentication() throws Exception {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        when(counts.getBatch(List.of(first, first, second))).thenReturn(List.of());
        String body = "{\"postIds\":[\"" + first + "\",\"" + first + "\",\"" + second + "\"]}";

        mvc.perform(post("/api/v1/comments/counts/batch")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(counts).getBatch(List.of(first, first, second));
    }

    @Test
    void singleCountReturnsLocalActiveCount() throws Exception {
        UUID postId = UUID.randomUUID();
        when(counts.get(postId)).thenReturn(new CommentCountResponse(postId, 7L));

        mvc.perform(get("/api/v1/posts/{postId}/comments/count", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postId").value(postId.toString()))
                .andExpect(jsonPath("$.data.commentCount").value(7));

        verify(counts).get(postId);
    }

    @Test
    void batchCountRejectsEmptyOrNullPostIds() throws Exception {
        mvc.perform(post("/api/v1/comments/counts/batch")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"postIds\":[]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/comments/counts/batch")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"postIds\":[null]}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(counts);
    }

    @Test
    void targetNotFoundUsesStableErrorCodeAndCorrelationId() throws Exception {
        UUID actorId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        String traceId = UUID.randomUUID().toString();
        String body = "{\"postId\":\"" + postId + "\",\"content\":\"hello\"}";
        when(create.execute(any())).thenThrow(new TargetNotFoundException("Post does not exist"));

        mvc.perform(post("/api/v1/comments")
                        .header("X-Auth-User-Id", actorId)
                        .header("X-Correlation-Id", traceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value(45003))
                .andExpect(jsonPath("$.traceId").value(traceId));
    }

    @Test
    void frameworkAndUnexpectedFailuresUseCorrectHttpStatuses() throws Exception {
        UUID actorId = UUID.randomUUID();
        String body = "{\"postId\":\"" + UUID.randomUUID() + "\",\"content\":\"hello\"}";

        mvc.perform(post("/api/v1/comments")
                        .header("X-Auth-User-Id", actorId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(body))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value(45010));

        mvc.perform(put("/api/v1/comments")
                        .header("X-Auth-User-Id", actorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(45009));

        when(create.execute(any())).thenThrow(new IllegalStateException("database detail must not leak"));
        mvc.perform(post("/api/v1/comments")
                        .header("X-Auth-User-Id", actorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(45999))
                .andExpect(jsonPath("$.message").value("Unexpected server error"));
    }
}
