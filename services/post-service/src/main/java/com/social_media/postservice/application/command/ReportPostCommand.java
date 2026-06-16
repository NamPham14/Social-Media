package com.social_media.postservice.application.command;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.model.report.valueobject.ReportReason;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ReportPostCommand {
    UUID postId;
    UUID reporterId;
    ReportReason reason;
    String description;

    public ReportPostCommand(UUID postId, UUID reporterId, ReportReason reason, String description) {
        this.postId = postId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.description = description;
        validate();
    }

    private void validate() {
        if (postId == null || reporterId == null || reason == null) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}

