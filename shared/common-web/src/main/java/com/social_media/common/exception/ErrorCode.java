package com.social_media.common.exception;


import org.springframework.http.HttpStatus;


public interface ErrorCode {

    int getCode();

    String getMessage();

    HttpStatus getHttpStatus();

    ErrorCode UNAUTHENTICATED = new ErrorCode() {
        @Override public int getCode() { return 10011; }
        @Override public String getMessage() { return "Unauthenticated"; }
        @Override public HttpStatus getHttpStatus() { return HttpStatus.UNAUTHORIZED; }
    };
}
