package com.social_media.postservice.domain.exception;

import com.social_media.common.base.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    CLOUDINARY_ERROR(
            9999,
            "Uncategorized error",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    EMPTY_RESOURCE(
            4004,
            "Resource is empty",
            HttpStatus.NOT_FOUND
    ),

    RESOURCE_NOT_FOUND(
            4004,
            "Resource not found",
            HttpStatus.NOT_FOUND
    ),

    UNAUTHORIZED_ACTION(
            4001,
            "Unauthorized action",
            HttpStatus.UNAUTHORIZED
    );

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}