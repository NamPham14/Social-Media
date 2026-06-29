package com.social_media.common.exception;

public class BusinessRuleViolationException extends DomainException {
    private final int ruleCode;

    public BusinessRuleViolationException(int ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public int getRuleCode() { return ruleCode; }
}
