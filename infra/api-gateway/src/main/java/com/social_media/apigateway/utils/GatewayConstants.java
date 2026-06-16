package com.social_media.apigateway.utils;

public class GatewayConstants {
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String  HEADER_AUTHOR_ID = "Authorization";

    public static final int  ORDER_CORRELATION_FILTER = -200;
    public static final int  ORDER_LOGGING_FILTER = -150;
    public static final int  ORDER_RATE_LIMIT_FILTER = -100;


}
