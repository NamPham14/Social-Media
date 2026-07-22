package com.social_media.interactionservice.api.controller;

import com.social_media.interactionservice.api.InteractionExceptionHandler;
import com.social_media.interactionservice.api.dto.CounterResponse;
import com.social_media.interactionservice.api.dto.InteractionSummaryResponse;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.usecase.*;
import com.social_media.interactionservice.domain.exception.TargetNotFoundException;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.infrastructure.web.CorrelationIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InteractionControllerTest {
    private CreateInteractionUseCase create;
    private RemoveInteractionUseCase remove;
    private GetCountersUseCase counters;
    private GetInteractionSummariesUseCase summaries;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        create = mock(CreateInteractionUseCase.class);
        remove = mock(RemoveInteractionUseCase.class);
        counters = mock(GetCountersUseCase.class);
        summaries = mock(GetInteractionSummariesUseCase.class);
        InteractionController controller = new InteractionController(
                create, remove, mock(FindActorReactionsUseCase.class), counters, summaries,
                mock(GetReactorsUseCase.class));
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new InteractionExceptionHandler())
                .addFilters(new CorrelationIdFilter())
                .build();
    }

    @Test
    void bodyActorCannotSpoofHeaderActor() throws Exception {
        UUID actor = UUID.randomUUID();
        String body = "{\"userId\":\"" + UUID.randomUUID() + "\",\"targetType\":\"POST\",\"targetId\":\""
                + UUID.randomUUID() + "\",\"reactionType\":\"LIKE\"}";
        mvc.perform(post("/api/v1/interactions").header("X-Auth-User-Id", actor)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));
        ArgumentCaptor<CreateInteractionCommand> captor = ArgumentCaptor.forClass(CreateInteractionCommand.class);
        verify(create).execute(captor.capture());
        assertThat(captor.getValue().actorId()).isEqualTo(actor);
    }

    @Test
    void bookmarkIsRejectedAndMissingActorIsBadRequest() throws Exception {
        String target = UUID.randomUUID().toString();
        String bookmark = "{\"targetType\":\"POST\",\"targetId\":\"" + target + "\",\"reactionType\":\"BOOKMARK\"}";
        mvc.perform(post("/api/v1/interactions").header("X-Auth-User-Id", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content(bookmark)).andExpect(status().isBadRequest());
        String like = "{\"targetType\":\"POST\",\"targetId\":\"" + target + "\",\"reactionType\":\"LIKE\"}";
        mvc.perform(post("/api/v1/interactions").contentType(MediaType.APPLICATION_JSON).content(like))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeBindsAllExplicitPathVariables() throws Exception {
        UUID actorId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        when(remove.execute(actorId, TargetType.COMMENT, targetId, ReactionType.LIKE)).thenReturn(true);

        mvc.perform(delete("/api/v1/interactions/{targetType}/{targetId}/{reactionType}",
                        TargetType.COMMENT, targetId, ReactionType.LIKE)
                        .header("X-Auth-User-Id", actorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        verify(remove).execute(actorId, TargetType.COMMENT, targetId, ReactionType.LIKE);
    }

    @Test
    void counterBindsExplicitTargetPathVariables() throws Exception {
        UUID targetId = UUID.randomUUID();
        when(counters.get(TargetType.POST, targetId))
                .thenReturn(new CounterResponse(TargetType.POST, targetId, 2));

        mvc.perform(get("/api/v1/interactions/counters/{targetType}/{targetId}", TargetType.POST, targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reactionCount").value(2));

        verify(counters).get(TargetType.POST, targetId);
    }

    @Test
    void batchSummaryReturnsCountsAndActorStateInOneRequest() throws Exception {
        UUID actorId = UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        when(summaries.getBatch(eq(actorId), anyList())).thenReturn(java.util.List.of(
                new InteractionSummaryResponse(TargetType.COMMENT, first, 4, true),
                new InteractionSummaryResponse(TargetType.COMMENT, second, 0, false)));

        String body = "{\"targets\":["
                + "{\"targetType\":\"COMMENT\",\"targetId\":\"" + first + "\"},"
                + "{\"targetType\":\"COMMENT\",\"targetId\":\"" + second + "\"}]}";
        mvc.perform(post("/api/v1/interactions/summaries/batch")
                        .header("X-Auth-User-Id", actorId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].reactionCount").value(4))
                .andExpect(jsonPath("$.data[0].likedByMe").value(true))
                .andExpect(jsonPath("$.data[1].likedByMe").value(false));

        verify(summaries).getBatch(eq(actorId), argThat(targets -> targets.size() == 2));
    }

    @Test
    void batchSummaryAllowsAnonymousCaller() throws Exception {
        UUID targetId = UUID.randomUUID();
        when(summaries.getBatch(eq(null), anyList())).thenReturn(java.util.List.of(
                new InteractionSummaryResponse(TargetType.COMMENT, targetId, 4, false)));
        String body = "{\"targets\":[{\"targetType\":\"COMMENT\",\"targetId\":\"" + targetId + "\"}]}";

        mvc.perform(post("/api/v1/interactions/summaries/batch")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].likedByMe").value(false));

        verify(summaries).getBatch(eq(null), anyList());
    }

    @Test
    void targetNotFoundUsesStableErrorCodeAndCorrelationId() throws Exception {
        UUID actorId = UUID.randomUUID();
        String traceId = UUID.randomUUID().toString();
        String body = "{\"targetType\":\"POST\",\"targetId\":\"" + UUID.randomUUID()
                + "\",\"reactionType\":\"LIKE\"}";
        when(create.execute(any())).thenThrow(new TargetNotFoundException("Post does not exist"));

        mvc.perform(post("/api/v1/interactions")
                        .header("X-Auth-User-Id", actorId)
                        .header("X-Correlation-Id", traceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value(46002))
                .andExpect(jsonPath("$.traceId").value(traceId));
    }

    @Test
    void frameworkAndUnexpectedFailuresUseCorrectHttpStatuses() throws Exception {
        UUID actorId = UUID.randomUUID();
        String body = "{\"targetType\":\"POST\",\"targetId\":\"" + UUID.randomUUID()
                + "\",\"reactionType\":\"LIKE\"}";

        mvc.perform(post("/api/v1/interactions")
                        .header("X-Auth-User-Id", actorId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(body))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value(46007));

        mvc.perform(put("/api/v1/interactions")
                        .header("X-Auth-User-Id", actorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(46006));

        when(create.execute(any())).thenThrow(new IllegalStateException("database detail must not leak"));
        mvc.perform(post("/api/v1/interactions")
                        .header("X-Auth-User-Id", actorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(46999))
                .andExpect(jsonPath("$.message").value("Unexpected server error"));
    }
}
