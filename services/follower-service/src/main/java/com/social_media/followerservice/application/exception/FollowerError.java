package com.social_media.followerservice.application.exception;

import com.social_media.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FollowerError implements ErrorCode {
    FOLLOW_NOT_FOUND(2001, "Follow relationship not found", HttpStatus.NOT_FOUND),
    DUPLICATE_FOLLOW(2002, "Already following this user", HttpStatus.CONFLICT),
    CANNOT_FOLLOW_SELF(2003, "Cannot follow yourself", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(2004, "Invalid input data", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}