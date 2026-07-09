package com.social_media.apigateway.utils;

public class GatewayConstants {
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    // Headers
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String HEADER_USER_ID = "X-Auth-User-Id";
    public static final String HEADER_USER_NAME = "X-Auth-Username";
    public static final String HEADER_USER_ROLES = "X-Auth-Roles";
    public static final String HEADER_AUTHOR = "Authorization";

    // Filter Orders
    public static final int ORDER_CORRELATION_FILTER = -200;
    public static final int ORDER_LOGGING_FILTER = -100;
    public static final int ORDER_SECURITY_HEADER_FILTER = -90;
    public static final int ORDER_RATE_LIMIT_FILTER = -50;
    public static final int ORDER_JWT_AUTH_FILTER = -20;

    private GatewayConstants() {
        // Prevent instantiation
    }
}