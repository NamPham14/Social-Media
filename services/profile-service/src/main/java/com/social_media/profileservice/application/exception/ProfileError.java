package com.social_media.profileservice.application.exception;

public enum ProfileError {
    PROFILE_NOT_FOUND(1002, "Không tìm thấy hồ sơ người dùng"),
    PROFILE_ALREADY_EXISTS(1001, "Hồ sơ người dùng đã tồn tại"),
    INVALID_INPUT(1003, "Dữ liệu đầu vào không hợp lệ");

    private final int code;
    private final String message;

    ProfileError(int code, String message) {
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
