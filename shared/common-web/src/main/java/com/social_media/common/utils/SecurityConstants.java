package com.social_media.common.utils;

/**
 * Chứa các hằng số dùng chung liên quan đến Bảo mật (Security) cho toàn bộ hệ thống Microservices.
 */
public class SecurityConstants {
    
    /**
     * Tên Header chứa ID của User đang đăng nhập.
     * Quy trình:
     * 1. Client gửi request kèm JWT Token lên API Gateway.
     * 2. API Gateway xác thực Token, trích xuất userId và nhét vào Header này.
     * 3. API Gateway chuyển tiếp request (kèm Header) xuống các Service con (Profile, Post...).
     * 4. Các Service con đọc Header này để biết User nào đang thực hiện request.
     */
    public static final String HEADER_USER_ID = "X-Auth-User-Id";
}
