package com.social_media.postservice.application.command;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.domain.exception.ErrorCode;
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
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
