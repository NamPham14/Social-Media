package com.social_media.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String message;
    private int status;
    private T data;

// Hàm gọi khi Thành Công (Tự động set mã 1000 và Status 200)
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message(message)
                .data(data)
                .build();
    }
    //  Hàm gọi khi Thành Công nhưng không có Data trả về (VD: Logout)
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message(message)
                .build();
    }
}
