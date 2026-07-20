package com.social_media.interactionservice.api;

import com.social_media.interactionservice.api.exception.InteractionErrorCode;
import com.social_media.common.api.ApiResponse;
import com.social_media.interactionservice.domain.exception.*;
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
public class InteractionExceptionHandler {
    @ExceptionHandler(TargetNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> notFound(RuntimeException ex) {
        return error(InteractionErrorCode.TARGET_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ReactionConflictException.class)
    ResponseEntity<ApiResponse<Void>> conflict(RuntimeException ex) {
        return error(InteractionErrorCode.REACTION_CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DependencyUnavailableException.class)
    ResponseEntity<ApiResponse<Void>> unavailable(RuntimeException ex) {
        return error(InteractionErrorCode.DEPENDENCY_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ServletRequestBindingException.class,
            HttpMessageNotReadableException.class})
    ResponseEntity<ApiResponse<Void>> badRequest(Exception ex) {
        return error(InteractionErrorCode.INVALID_REQUEST, "Missing or invalid request value");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage()).collect(Collectors.joining(", "));
        return error(InteractionErrorCode.VALIDATION_FAILED, message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ApiResponse<Void>> methodValidation(HandlerMethodValidationException ex) {
        return error(InteractionErrorCode.VALIDATION_FAILED, InteractionErrorCode.VALIDATION_FAILED.message());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> routeNotFound(NoResourceFoundException ex) {
        return error(InteractionErrorCode.ROUTE_NOT_FOUND, InteractionErrorCode.ROUTE_NOT_FOUND.message());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<Void>> methodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return error(InteractionErrorCode.METHOD_NOT_ALLOWED, InteractionErrorCode.METHOD_NOT_ALLOWED.message());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiResponse<Void>> unsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return error(InteractionErrorCode.UNSUPPORTED_MEDIA_TYPE, InteractionErrorCode.UNSUPPORTED_MEDIA_TYPE.message());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> unexpected(Exception ex) {
        log.error("Unhandled interaction-service exception traceId={}", MDC.get("correlationId"), ex);
        return error(InteractionErrorCode.INTERNAL_ERROR, InteractionErrorCode.INTERNAL_ERROR.message());
    }

    private ResponseEntity<ApiResponse<Void>> error(InteractionErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.status()).body(ApiResponse.<Void>builder()
                .status(errorCode.status().value()).code(errorCode.code()).message(message)
                .traceId(MDC.get("correlationId")).build());
    }
}
