package com.social_media.followerservice.application.exception;

public enum FollowerError {
    FOLLOW_NOT_FOUND(2001, "Không tìm thấy mối quan hệ follow"),
    DUPLICATE_FOLLOW(2002, "Đã follow người dùng này rồi"),
    CANNOT_FOLLOW_SELF(2003, "Không thể follow chính mình"),
    INVALID_INPUT(2004, "Dữ liệu đầu vào không hợp lệ");

    private final int code;
    private final String message;

    FollowerError(int code, String message) {
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
