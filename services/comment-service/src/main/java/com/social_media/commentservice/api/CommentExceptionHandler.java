package com.social_media.commentservice.api;

import com.social_media.commentservice.domain.exception.*;
import com.social_media.common.api.ApiResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CommentExceptionHandler {
    @ExceptionHandler(CommentNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> notFound(RuntimeException ex) { return error(HttpStatus.NOT_FOUND, ex.getMessage()); }

    @ExceptionHandler(CommentAccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> forbidden(RuntimeException ex) { return error(HttpStatus.FORBIDDEN, ex.getMessage()); }

    @ExceptionHandler(InternalAccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> internalForbidden(RuntimeException ex) { return error(HttpStatus.FORBIDDEN, ex.getMessage()); }

    @ExceptionHandler({InvalidCommentException.class, CommentAlreadyDeletedException.class})
    ResponseEntity<ApiResponse<Void>> conflict(RuntimeException ex) { return error(HttpStatus.CONFLICT, ex.getMessage()); }

    @ExceptionHandler(TargetNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> targetNotFound(RuntimeException ex) { return error(HttpStatus.NOT_FOUND, ex.getMessage()); }

    @ExceptionHandler(DependencyUnavailableException.class)
    ResponseEntity<ApiResponse<Void>> unavailable(RuntimeException ex) { return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()); }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class})
    ResponseEntity<ApiResponse<Void>> badRequest(Exception ex) { return error(HttpStatus.BAD_REQUEST, "Missing or invalid request value"); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage()).collect(Collectors.joining(", "));
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> unexpected(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
    }

    private ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.<Void>builder()
                .status(status.value()).code(status.value()).message(message)
                .traceId(MDC.get("correlationId")).build());
    }
}
