package com.social_media.profileservice.api.exception;

import com.social_media.common.base.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProfileErrorCode implements BaseErrorCode {
    PROFILE_NOT_FOUND(1002, "Không tìm thấy hồ sơ người dùng",  HttpStatus.NOT_FOUND),
    PROFILE_ALREADY_EXISTS(1001, "Hồ sơ người dùng đã tồn tại",  HttpStatus.CONFLICT),
    INVALID_INPUT(1003, "Dữ liệu đầu vào không hợp lệ",  HttpStatus.BAD_REQUEST);




    private final int code;
    private final String message;
    private final HttpStatus status;

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
        return this.status;
    }
}
