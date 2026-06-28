package com.social_media.identityservice.api.exception;

import com.social_media.common.api.ApiResponse;
import com.social_media.identityservice.domain.model.user.exception.InvalidUserIdentityException;
import com.social_media.identityservice.infrastructure.exception.ProfileServiceDownException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IdentityExceptionHandler {

    /**
     * Exception handler sẽ bắt exception ở domain
     */

    @ExceptionHandler(InvalidUserIdentityException.class)
    public ResponseEntity<ApiResponse<Object>> handleDomainException(InvalidUserIdentityException e) {

        // Gói lại thành JSON bằng mã lỗi bạn tự định nghĩa
        ApiResponse<Object> response = ApiResponse.builder()
                .status(IdentityErrorCode.INVALID_PASSWORD.getHttpStatus().value())
                .code(IdentityErrorCode.INVALID_PASSWORD.getCode())
                .message(e.getMessage()) // Lấy câu chửi từ Domain ("Username quá ngắn")
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     *  Dành cho tầng Infrastructure (Lỗi Feign)
     * @param
     * @return
     */
    @ExceptionHandler(ProfileServiceDownException.class)
    public ResponseEntity<ApiResponse<Object>> handleInfraException(ProfileServiceDownException e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .status(503)
                .code(2001)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(503).body(response);
    }
}
