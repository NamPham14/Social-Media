package com.social_media.interactionservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.interactionservice.api.dto.BatchPostLikedRequest;
import com.social_media.interactionservice.api.dto.BatchPostReactionRequest;
import com.social_media.interactionservice.api.dto.CreateInteractionRequest;
import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.api.dto.BatchCounterRequest;
import com.social_media.interactionservice.api.dto.CounterResponse;
import com.social_media.interactionservice.api.dto.InteractionSummaryResponse;
import com.social_media.interactionservice.api.dto.PostLikedResponse;
import com.social_media.interactionservice.api.dto.PostReactionResponse;
import com.social_media.interactionservice.api.dto.ReactorResponse;
import com.social_media.common.api.PageResponse;
import com.social_media.interactionservice.api.path.ApiPath;
import com.social_media.interactionservice.application.command.CreateInteractionCommand;
import com.social_media.interactionservice.application.usecase.CreateInteractionUseCase;
import com.social_media.interactionservice.application.usecase.FindActorReactionsUseCase;
import com.social_media.interactionservice.application.usecase.GetCountersUseCase;
import com.social_media.interactionservice.application.usecase.GetInteractionSummariesUseCase;
import com.social_media.interactionservice.application.usecase.GetReactorsUseCase;
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

    private static final int SUCCESS_CODE = 1000;

    private final CreateInteractionUseCase createInteractionUseCase;
    private final RemoveInteractionUseCase removeInteractionUseCase;
    private final FindActorReactionsUseCase findActorReactionsUseCase;
    private final GetCountersUseCase getCountersUseCase;
    private final GetInteractionSummariesUseCase getInteractionSummariesUseCase;
    private final GetReactorsUseCase getReactorsUseCase;

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
                .code(SUCCESS_CODE)
                .status(HttpStatus.OK.value())
                .message("Create Interaction Success")
                .data(createInteractionUseCase.execute(command))
                .build();
    }

    @DeleteMapping(ApiPath.INTERACTION)
    public ApiResponse<Boolean> removeInteraction(
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @PathVariable("targetType") TargetType targetType,
            @PathVariable("targetId") UUID targetId,
            @PathVariable("reactionType") ReactionType reactionType) {
        boolean removed = removeInteractionUseCase.execute(actorId, targetType, targetId, reactionType);
        return ApiResponse.<Boolean>builder().code(SUCCESS_CODE).status(200)
                .message(removed ? "Remove Interaction Success" : "Interaction Already Absent")
                .data(removed).build();
    }

    @GetMapping(ApiPath.MY_INTERACTIONS)
    public ApiResponse<List<InteractionResponse>> myInteractions(
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @PathVariable("targetType") TargetType targetType,
            @PathVariable("targetId") UUID targetId) {
        return ApiResponse.<List<InteractionResponse>>builder().code(SUCCESS_CODE).status(200)
                .message("Get Actor Interactions Success")
                .data(findActorReactionsUseCase.execute(actorId, targetType, targetId)).build();
    }

    @GetMapping(ApiPath.COUNTER)
    public ApiResponse<CounterResponse> counter(
            @PathVariable("targetType") TargetType targetType,
            @PathVariable("targetId") UUID targetId) {
        return ApiResponse.<CounterResponse>builder().code(SUCCESS_CODE).status(200).message("Get Counter Success")
                .data(getCountersUseCase.get(targetType, targetId)).build();
    }

    @PostMapping(ApiPath.COUNTERS_BATCH)
    public ApiResponse<List<CounterResponse>> counters(@Valid @RequestBody BatchCounterRequest request) {
        return ApiResponse.<List<CounterResponse>>builder().code(SUCCESS_CODE).status(200).message("Get Counters Success")
                .data(getCountersUseCase.getBatch(request.targets())).build();
    }

    @GetMapping(ApiPath.SUMMARY)
    public ApiResponse<InteractionSummaryResponse> summary(
            @RequestHeader(value = "X-Auth-User-Id", required = false) UUID actorId,
            @PathVariable("targetType") TargetType targetType,
            @PathVariable("targetId") UUID targetId) {
        return ApiResponse.<InteractionSummaryResponse>builder().code(SUCCESS_CODE).status(200)
                .message("Get Interaction Summary Success")
                .data(getInteractionSummariesUseCase.get(actorId, targetType, targetId)).build();
    }

    @PostMapping(ApiPath.SUMMARIES_BATCH)
    public ApiResponse<List<InteractionSummaryResponse>> summaries(
            @RequestHeader(value = "X-Auth-User-Id", required = false) UUID actorId,
            @Valid @RequestBody BatchCounterRequest request) {
        return ApiResponse.<List<InteractionSummaryResponse>>builder().code(SUCCESS_CODE).status(200)
                .message("Get Interaction Summaries Success")
                .data(getInteractionSummariesUseCase.getBatch(actorId, request.targets())).build();
    }

    @GetMapping(ApiPath.REACTORS)
    public ApiResponse<PageResponse<ReactorResponse>> reactors(
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @PathVariable("targetType") TargetType targetType,
            @PathVariable("targetId") UUID targetId,
            @org.springframework.web.bind.annotation.RequestParam(name = "page", defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(name = "size", defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return ApiResponse.<PageResponse<ReactorResponse>>builder().code(SUCCESS_CODE).status(200)
                .message("Get Reactors Success")
                .data(getReactorsUseCase.execute(actorId, targetType, targetId, Math.max(page, 0), safeSize)).build();
    }

    // Hiếu thêm — endpoint mới chuyên cho post-service
    @PostMapping(ApiPath.POST_REACTION_COUNTS)
    public ApiResponse<List<PostReactionResponse>> getPostReactionCounts(
            @Valid @RequestBody BatchPostReactionRequest request) {
        return ApiResponse.<List<PostReactionResponse>>builder()
                .code(SUCCESS_CODE).status(HttpStatus.OK.value())
                .message("Get Post Reaction Counts Success")
                .data(getCountersUseCase.getBatchByPostIds(request.postIds()))
                .build();
    }

    // Hiếu thêm — endpoint check likedByMe
    @PostMapping(ApiPath.POST_LIKED_BY_ME)
    public ApiResponse<List<PostLikedResponse>> getPostLikedByMe(
            @RequestHeader("X-Auth-User-Id") UUID actorId,
            @Valid @RequestBody BatchPostLikedRequest request) {
        return ApiResponse.<List<PostLikedResponse>>builder()
                .code(SUCCESS_CODE).status(HttpStatus.OK.value())
                .message("Get Post Liked By Me Success")
                .data(getInteractionSummariesUseCase.getBatchLikedByMe(actorId, request.postIds()))
                .build();
    }
}
