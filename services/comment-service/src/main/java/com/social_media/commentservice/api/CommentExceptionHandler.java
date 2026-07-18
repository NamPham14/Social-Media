package com.social_media.commentservice.api;

import com.social_media.commentservice.api.exception.CommentErrorCode;
import com.social_media.commentservice.domain.exception.*;
import com.social_media.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CommentExceptionHandler {
    @ExceptionHandler(CommentNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> notFound(RuntimeException ex) {
        return error(CommentErrorCode.COMMENT_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CommentAccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> forbidden(RuntimeException ex) {
        return error(CommentErrorCode.COMMENT_ACCESS_DENIED, ex.getMessage());
    }

    @ExceptionHandler(InternalAccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> internalForbidden(RuntimeException ex) {
        return error(CommentErrorCode.INTERNAL_ACCESS_DENIED, ex.getMessage());
    }

    @ExceptionHandler({InvalidCommentException.class, CommentAlreadyDeletedException.class})
    ResponseEntity<ApiResponse<Void>> conflict(RuntimeException ex) {
        return error(CommentErrorCode.COMMENT_CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TargetNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> targetNotFound(RuntimeException ex) {
        return error(CommentErrorCode.TARGET_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DependencyUnavailableException.class)
    ResponseEntity<ApiResponse<Void>> unavailable(RuntimeException ex) {
        return error(CommentErrorCode.DEPENDENCY_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ServletRequestBindingException.class,
            HttpMessageNotReadableException.class})
    ResponseEntity<ApiResponse<Void>> badRequest(Exception ex) {
        return error(CommentErrorCode.INVALID_REQUEST, "Missing or invalid request value");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage()).collect(Collectors.joining(", "));
        return error(CommentErrorCode.VALIDATION_FAILED, message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ApiResponse<Void>> methodValidation(HandlerMethodValidationException ex) {
        return error(CommentErrorCode.VALIDATION_FAILED, CommentErrorCode.VALIDATION_FAILED.message());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> routeNotFound(NoResourceFoundException ex) {
        return error(CommentErrorCode.ROUTE_NOT_FOUND, CommentErrorCode.ROUTE_NOT_FOUND.message());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<Void>> methodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return error(CommentErrorCode.METHOD_NOT_ALLOWED, CommentErrorCode.METHOD_NOT_ALLOWED.message());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiResponse<Void>> unsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return error(CommentErrorCode.UNSUPPORTED_MEDIA_TYPE, CommentErrorCode.UNSUPPORTED_MEDIA_TYPE.message());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> unexpected(Exception ex) {
        log.error("Unhandled comment-service exception traceId={}", MDC.get("correlationId"), ex);
        return error(CommentErrorCode.INTERNAL_ERROR, CommentErrorCode.INTERNAL_ERROR.message());
    }

    private ResponseEntity<ApiResponse<Void>> error(CommentErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.status()).body(ApiResponse.<Void>builder()
                .status(errorCode.status().value()).code(errorCode.code()).message(message)
                .traceId(MDC.get("correlationId")).build());
    }
}
