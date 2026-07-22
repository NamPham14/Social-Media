package com.social_media.interactionservice.application.usecase;

import com.social_media.common.api.PageResponse;
import com.social_media.interactionservice.api.dto.ReactorResponse;
import com.social_media.interactionservice.application.port.out.TargetAvailabilityPort;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetReactorsUseCaseImpl implements GetReactorsUseCase {
    private final InteractionRepository interactionRepository;
    private final TargetAvailabilityPort targetAvailabilityPort;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReactorResponse> execute(
            UUID actorId, TargetType targetType, UUID targetId, int page, int size) {
        targetAvailabilityPort.getAvailable(targetType, targetId, actorId);
        var result = interactionRepository.findReactors(targetType, targetId, page, size);
        var items = result.content().stream()
                .map(row -> new ReactorResponse(row.getUserId(), row.getReactionType(), row.getCreatedAt()))
                .toList();
        return PageResponse.<ReactorResponse>builder()
                .items(items).currentPage(result.page()).pageSize(result.size())
                .totalElements(result.totalElements()).totalPages(result.totalPages())
                .hasNext(result.page() + 1 < result.totalPages()).hasPrevious(result.page() > 0).build();
    }
}
