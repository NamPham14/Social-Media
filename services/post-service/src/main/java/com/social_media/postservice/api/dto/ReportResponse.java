package com.social_media.postservice.api.dto;

import com.social_media.postservice.domain.model.report.aggregate.Report;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReportResponse {

    private UUID id;
    private UUID postId;
    private UUID reporterId;
    private String reason;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public static ReportResponse from(Report report) {
        ReportResponse res = new ReportResponse();
        res.id = report.getId();
        res.postId = report.getPostId();
        res.reporterId = report.getReporterId();
        res.reason = report.getReason().name();
        res.description = report.getDescription();
        res.status = report.getStatus().name();
        res.createdAt = report.getCreatedAt();
        return res;
    }
}

