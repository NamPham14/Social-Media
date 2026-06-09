package com.social_media.postservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.api.dto.ReportPostRequest;
import com.social_media.postservice.api.dto.ReportResponse;
import com.social_media.postservice.api.dto.ReviewReportRequest;
import com.social_media.postservice.api.path.ApiPath;
import com.social_media.postservice.application.command.ReportPostCommand;
import com.social_media.postservice.application.command.ReviewReportCommand;
import com.social_media.postservice.application.usecase.DismissReportUseCase;
import com.social_media.postservice.application.usecase.GetReportedPostsUseCase;
import com.social_media.postservice.application.usecase.RemoveReportedPostUseCase;
import com.social_media.postservice.application.usecase.ReportPostUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class ReportController {

    private final ReportPostUseCase reportPostUseCase;
    private final GetReportedPostsUseCase getReportedPostsUseCase;
    private final DismissReportUseCase dismissReportUseCase;
    private final RemoveReportedPostUseCase removeReportedPostUseCase;

    @PostMapping(ApiPath.POST_REPORT)
    public ApiResponse<Void> reportPost(
            @PathVariable("postId") UUID postId,
            @Valid @RequestBody ReportPostRequest request) {
        ReportPostCommand command = new ReportPostCommand(
                postId,
                request.getReporterId(),
                request.getReason(),
                request.getDescription()
        );
        reportPostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Report Post Success")
                .build();
    }

    @GetMapping(ApiPath.REPORTS)
    public ApiResponse<Page<ReportResponse>> getReportedPosts(Pageable pageable) {
        Page<ReportResponse> result = getReportedPostsUseCase.execute(pageable)
                .map(ReportResponse::from);
        return ApiResponse.<Page<ReportResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get Reported Posts Success")
                .data(result)
                .build();
    }

    @PutMapping(ApiPath.REPORT_DISMISS)
    public ApiResponse<Void> dismissReport(
            @PathVariable("reportId") UUID reportId,
            @Valid @RequestBody ReviewReportRequest request) {
        ReviewReportCommand command = new ReviewReportCommand(
                reportId,
                request.getAdminId()
        );
        dismissReportUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Dismiss Report Success")
                .build();
    }

    @PutMapping(ApiPath.REPORT_REMOVE)
    public ApiResponse<Void> removeReportedPost(
            @PathVariable("reportId") UUID reportId,
            @Valid @RequestBody ReviewReportRequest request) {
        ReviewReportCommand command = new ReviewReportCommand(
                reportId,
                request.getAdminId()
        );
        removeReportedPostUseCase.execute(command);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Remove Reported Post Success")
                .build();
    }
}
