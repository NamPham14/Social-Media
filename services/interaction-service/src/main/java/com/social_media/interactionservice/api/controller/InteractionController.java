package com.social_media.interactionservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.interactionservice.api.dto.CreateInteractionRequest;
import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.api.path.ApiPath;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.usecase.CreateInteractionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class InteractionController {

    private final CreateInteractionUseCase createInteractionUseCase;

    @PostMapping(ApiPath.INTERACTIONS)
    public ApiResponse<InteractionResponse> createInteraction(@Valid @RequestBody CreateInteractionRequest request) {
        CreateInteractionCommand command = new CreateInteractionCommand(
                request.getUserId(),
                request.getTargetType(),
                request.getTargetId(),
                request.getReactionType()
        );

        return ApiResponse.<InteractionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Create Interaction Success")
                .data(createInteractionUseCase.execute(command))
                .build();
    }
}
