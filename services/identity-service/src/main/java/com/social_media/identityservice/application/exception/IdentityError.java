package com.social_media.identityservice.application.exception;

public enum IdentityError {
    USER_EXISTED(1001, "Người dùng đã tồn tại"),
    USER_NOT_FOUND(1002, "Không tìm thấy người dùng"),
    INVALID_PASSWORD(1004, "Mật khẩu không hợp lệ"),
    UNAUTHENTICATED(1005, "Chưa xác thực"),
    UNAUTHORIZED(1006, "Không có quyền truy cập");

    private final int code;
    private final String message;

    IdentityError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
