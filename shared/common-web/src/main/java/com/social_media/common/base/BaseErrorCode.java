package com.social_media.common.base;

import org.springframework.http.HttpStatus;


public interface BaseErrorCode {

    int getCode();

    String getMessage();

    HttpStatus getHttpStatus();
}
