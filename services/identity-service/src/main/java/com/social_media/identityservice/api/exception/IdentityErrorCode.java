package com.social_media.identityservice.api.exception;

import com.social_media.common.base.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum IdentityErrorCode implements BaseErrorCode {
    USER_EXISTED(1001, "Người dùng đã tồn tại", HttpStatus.CONFLICT),
    USER_NOT_FOUND(1002, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(1004, "Mật khẩu không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1005, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "Không có quyền truy cập", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
