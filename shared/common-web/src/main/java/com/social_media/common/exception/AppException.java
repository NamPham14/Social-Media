package com.social_media.common.exception;

import com.social_media.common.base.BaseErrorCode;

public class AppException extends DomainException {
    private final BaseErrorCode errorCode;

    public AppException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }
}
