package com.social_media.identityservice.domain.model.user.exception;

import com.social_media.common.exception.AppException;
import com.social_media.identityservice.api.exception.IdentityErrorCode;

/**
 * Lỗi vi phạm quy tắc nghiệp vụ của Domain User (VD: dữ liệu không hợp lệ).
 * Lớp này đại diện cho Domain Exception.
 */
public class InvalidUserIdentityException extends AppException {
    public InvalidUserIdentityException(String message) {
        // Bạn có thể định nghĩa thêm mã lỗi cụ thể trong IdentityErrorCode nếu cần
        super(IdentityErrorCode.INVALID_PASSWORD); 
    }
}
