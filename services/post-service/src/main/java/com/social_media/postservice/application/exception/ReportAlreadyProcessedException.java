package com.social_media.postservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class ReportAlreadyProcessedException extends BusinessRuleViolationException {
    public ReportAlreadyProcessedException() {
        super(PostError.REPORT_ALREADY_PROCESSED.getCode(), PostError.REPORT_ALREADY_PROCESSED.getMessage());
    }
}
