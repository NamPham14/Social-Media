package com.social_media.postservice.application.exception;

public enum PostError {
    CLOUDINARY_ERROR(9999, "Lỗi tải ảnh/video"),
    EMPTY_RESOURCE(4004, "Dữ liệu trống"),
    RESOURCE_NOT_FOUND(4004, "Không tìm thấy dữ liệu (Post/Report)"),
    UNAUTHORIZED_ACTION(4001, "Không có quyền thực hiện hành động này"),
    REPORT_ALREADY_PROCESSED(4002, "Báo cáo này đã được xử lý"),
    DUPLICATE_RESOURCE(4009, "Dữ liệu đã tồn tại (VD: Đã bookmark)");

    private final int code;
    private final String message;

    PostError(int code, String message) {
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
