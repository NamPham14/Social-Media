package com.social_media.postservice.api.controller;

import com.social_media.postservice.api.dto.ApiResponse;
import com.social_media.postservice.domain.exception.AppException;
import com.social_media.postservice.domain.exception.EmptyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(ex.getHttpStatus().value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(apiResponse);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {

        String message = String.format("Tham số '%s' truyền vào sai kiểu dữ liệu. Yêu cầu kiểu: %s",
                ex.getName(), ex.getRequiredType().getSimpleName());

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(HttpStatus.BAD_REQUEST.value()) // Mã 400
                .message(message) // Ví dụ: "Tham số 'userId' truyền vào sai kiểu dữ liệu. Yêu cầu kiểu: UUID"
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
