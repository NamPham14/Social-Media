package com.social_media.common.exception;

import com.social_media.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL EXCEPTION")
public class GlobalExceptionHandler {

    //  Lỗi không tìm thấy dữ liệu (Tầng Application/Domain ném)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, 4040, ex.getMessage());
    }

    //  Lỗi vi phạm nghiệp vụ (Tầng Domain ném)
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessRule(BusinessRuleViolationException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getRuleCode(), ex.getMessage());
    }

    //  Lỗi dịch vụ không khả dụng
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Object>> handleServiceUnavailable(ServiceUnavailableException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, 2001, ex.getMessage());
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUncaughtException(Exception e) {
        log.error("Unhandled Exception. traceId={}", MDC.get("correlationId"), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "Uncategorized error");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String detailErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .collect(java.util.stream.Collectors.joining(", "));
        
        log.warn("Validation Failed: {}", detailErrors);
        return buildResponse(HttpStatus.BAD_REQUEST, 4000, detailErrors);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            ServletRequestBindingException.class,
            MaxUploadSizeExceededException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(Exception e) {
        log.warn("Bad Request Exception: {}", e.getMessage());
        String message = "Invalid request format or missing parameters";
        int code = 8888;

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

        return buildResponse(HttpStatus.BAD_REQUEST, code, message);
    }

    private ResponseEntity<ApiResponse<Object>> buildResponse(HttpStatus status, int code, String message) {
        ApiResponse<Object> response = ApiResponse.builder()
                .status(status.value())
                .code(code)
                .message(message)
                .traceId(MDC.get("correlationId"))
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
