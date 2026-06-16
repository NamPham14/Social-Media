package com.social_media.postservice.domain.model.post.aggregate;

import com.social_media.postservice.domain.model.post.valueobject.ReportReason;
import com.social_media.postservice.domain.model.post.valueobject.ReportStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Getter
public class Report {

    private UUID id;

    private UUID postId;

    private UUID reporterId;

    private ReportReason reason;

    private String description;

    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    private LocalDateTime createdAt;

    public static Report create(UUID postId, UUID reporterId, ReportReason reason, String description) {
        Report report = new Report();
        report.postId = postId;
        report.reporterId = reporterId;
        report.reason = reason;
        report.description = description;
        report.status = ReportStatus.PENDING;
        report.createdAt = LocalDateTime.now();
        return report;
    }

    public void dismiss() {
        if (this.status != ReportStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reports can be dismissed");
        }
        this.status = ReportStatus.DISMISSED;
    }

    public void actOn() {
        if (this.status != ReportStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reports can be acted on");
        }
        this.status = ReportStatus.ACTED_ON;
    }
}
