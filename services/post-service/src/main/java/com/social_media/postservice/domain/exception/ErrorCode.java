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
    ),

    REPORT_ALREADY_PROCESSED(
            4002,
            "Report has already been processed",
            HttpStatus.BAD_REQUEST
    ),

    DUPLICATE_RESOURCE(
            4009,
            "Resource already exists",
            HttpStatus.CONFLICT
    );

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}