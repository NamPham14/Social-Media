package com.social_media.profileservice.domain.exception;

import com.social_media.common.exception.AppException;
import com.social_media.common.exception.ErrorCode;

public class DuplicateProfileException extends AppException {
    public DuplicateProfileException() {
        super(ErrorCode.USER_EXISTED); // Tái sử dụng ErrorCode đã có hoặc bạn có thể định nghĩa thêm
    }
}
