package com.social_media.common.exception;

import com.social_media.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL EXCEPTION")
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(
                ApiResponse.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUncaughtException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .build()
        );
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            ServletRequestBindingException.class,
            MaxUploadSizeExceededException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(Exception e) {
        log.warn("Bad Request Exception: {}", e.getMessage());
        String message = "Invalid request format or missing parameters";
        int code = ErrorCode.INVALID_KEY.getCode();

        if (e instanceof MethodArgumentTypeMismatchException mismatch) {
            message = String.format("Parameter '%s' expects type '%s'",
                    mismatch.getName(), Objects.requireNonNull(mismatch.getRequiredType()).getSimpleName());
        } else if (e instanceof ServletRequestBindingException) {
            message = "Missing or invalid required header/parameter: " + e.getMessage();
            code = 400;
        } else if (e instanceof MaxUploadSizeExceededException) {
            message = "File size exceeds the limit";
            code = 400;
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .code(code)
                        .message(message)
                        .build()
        );
    }
}
