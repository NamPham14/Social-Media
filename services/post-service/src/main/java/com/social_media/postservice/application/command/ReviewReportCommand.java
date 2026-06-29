package com.social_media.postservice.application.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ReviewReportCommand {
    UUID reportId;
    UUID adminId;

    public ReviewReportCommand(UUID reportId, UUID adminId) {
        this.reportId = reportId;
        this.adminId = adminId;
        validate();
    }

    private void validate() {
        if (reportId == null || adminId == null) {
            throw new com.social_media.postservice.application.exception.ResourceNotFoundException();
        }
    }
}
