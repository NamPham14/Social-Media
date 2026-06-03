package com.social_media.common.exception;

import com.social_media.common.base.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(8888, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User already exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS(1003, "Username already exists", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Invalid password", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1005, "Unauthorized", HttpStatus.UNAUTHORIZED),
    USER_LIST_EMPTY(1006, "User list is empty", HttpStatus.NOT_FOUND),
    NOT_FOUND_ROLE(1007, "Role not found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(1008, "Email already exists", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1009, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1010, "Forbidden", HttpStatus.FORBIDDEN),
    EMAIL_LIMIT_EXCEEDED(1011,"Email limit exceed",HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(1012, "Unauthenticated", HttpStatus.UNAUTHORIZED),

   ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
