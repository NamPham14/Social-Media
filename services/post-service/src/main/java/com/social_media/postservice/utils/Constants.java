package com.social_media.postservice.utils;

public class Constants {
    public  static final String X_FORWARDED_FOR = "X-Forwarded-For";

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public  static final String HEADER_USER_ID = "X-User-ID";
    public  static final String HEADER_USER_NAME = "X-User-Email";
    public  static final String HEADER_USER_ROLES = "X-User-Roles";
    public  static final String HEADER_AUTHOR = "Authorization";



    // Order
    public  static final int ORDER_CORRELATION_FILTER = -200;
    public  static final int ORDER_LOGGING_FILTER = -100;
    public  static final int ORDER_JWT_AUTH_FILTER = -90;

}
