package com.social_media.interactionservice.api.controller;

import com.social_media.interactionservice.api.InteractionExceptionHandler;
import com.social_media.interactionservice.api.dto.CounterResponse;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.usecase.*;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InteractionControllerTest {
    private CreateInteractionUseCase create;
    private RemoveInteractionUseCase remove;
    private GetCountersUseCase counters;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        create = mock(CreateInteractionUseCase.class);
        remove = mock(RemoveInteractionUseCase.class);
        counters = mock(GetCountersUseCase.class);
        InteractionController controller = new InteractionController(
                create, remove, mock(FindActorReactionsUseCase.class), counters);
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new InteractionExceptionHandler()).build();
    }

    @Test
    void bodyActorCannotSpoofHeaderActor() throws Exception {
        UUID actor = UUID.randomUUID();
        String body = "{\"userId\":\"" + UUID.randomUUID() + "\",\"targetType\":\"POST\",\"targetId\":\""
                + UUID.randomUUID() + "\",\"reactionType\":\"LIKE\"}";
        mvc.perform(post("/api/v1/interactions").header("X-Auth-User-Id", actor)
                        .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
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
                .thenReturn(new CounterResponse(TargetType.POST, targetId, 2, 1));

        mvc.perform(get("/api/v1/interactions/counters/{targetType}/{targetId}", TargetType.POST, targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(2))
                .andExpect(jsonPath("$.data.clapCount").value(1));

        verify(counters).get(TargetType.POST, targetId);
    }
}
