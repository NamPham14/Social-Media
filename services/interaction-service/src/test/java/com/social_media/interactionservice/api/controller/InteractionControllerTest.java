package com.social_media.interactionservice.api.controller;

import com.social_media.interactionservice.api.InteractionExceptionHandler;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.usecase.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InteractionControllerTest {
    private CreateInteractionUseCase create;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        create = mock(CreateInteractionUseCase.class);
        InteractionController controller = new InteractionController(create, mock(RemoveInteractionUseCase.class),
                mock(FindActorReactionsUseCase.class), mock(GetCountersUseCase.class));
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
}
