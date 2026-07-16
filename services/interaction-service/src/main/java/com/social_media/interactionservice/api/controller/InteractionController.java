package com.social_media.interactionservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.interactionservice.api.dto.CreateInteractionRequest;
import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.api.dto.BatchCounterRequest;
import com.social_media.interactionservice.api.dto.CounterResponse;
import com.social_media.interactionservice.api.path.ApiPath;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.usecase.CreateInteractionUseCase;
import com.social_media.interactionservice.application.usecase.FindActorReactionsUseCase;
import com.social_media.interactionservice.application.usecase.GetCountersUseCase;
import com.social_media.interactionservice.application.usecase.RemoveInteractionUseCase;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class InteractionController {

    private final CreateInteractionUseCase createInteractionUseCase;
    private final RemoveInteractionUseCase removeInteractionUseCase;
    private final FindActorReactionsUseCase findActorReactionsUseCase;
    private final GetCountersUseCase getCountersUseCase;

    @PostMapping(ApiPath.INTERACTIONS)
    public ApiResponse<InteractionResponse> createInteraction(
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @Valid @RequestBody CreateInteractionRequest request) {
        CreateInteractionCommand command = new CreateInteractionCommand(
                actorId,
                request.getTargetType(),
                request.getTargetId(),
                request.getReactionType()
        );

        return ApiResponse.<InteractionResponse>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.value())
                .message("Create Interaction Success")
                .data(createInteractionUseCase.execute(command))
                .build();
    }

    @DeleteMapping(ApiPath.INTERACTION)
    public ApiResponse<Boolean> removeInteraction(
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @PathVariable TargetType targetType, @PathVariable UUID targetId,
            @PathVariable ReactionType reactionType) {
        boolean removed = removeInteractionUseCase.execute(actorId, targetType, targetId, reactionType);
        return ApiResponse.<Boolean>builder().code(200).status(200)
                .message(removed ? "Remove Interaction Success" : "Interaction Already Absent")
                .data(removed).build();
    }

    @GetMapping(ApiPath.MY_INTERACTIONS)
    public ApiResponse<List<InteractionResponse>> myInteractions(
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @PathVariable TargetType targetType, @PathVariable UUID targetId) {
        return ApiResponse.<List<InteractionResponse>>builder().code(200).status(200)
                .message("Get Actor Interactions Success")
                .data(findActorReactionsUseCase.execute(actorId, targetType, targetId)).build();
    }

    @GetMapping(ApiPath.COUNTER)
    public ApiResponse<CounterResponse> counter(@PathVariable TargetType targetType, @PathVariable UUID targetId) {
        return ApiResponse.<CounterResponse>builder().code(200).status(200).message("Get Counter Success")
                .data(getCountersUseCase.get(targetType, targetId)).build();
    }

    @PostMapping(ApiPath.COUNTERS_BATCH)
    public ApiResponse<List<CounterResponse>> counters(@Valid @RequestBody BatchCounterRequest request) {
        return ApiResponse.<List<CounterResponse>>builder().code(200).status(200).message("Get Counters Success")
                .data(getCountersUseCase.getBatch(request.targets())).build();
    }
}
