package com.social_media.identityservice.api.controller;

public class ApiPath {
    public  static  final  String BASE ="/api/v1/identity";
    public  static  final  String USERS = "/users";
    public static final String AUTH = "/auth";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public  static  final  String SEARCHING = BASE + "/users/{keyword}";
    public  static  final  String REFRESH_TOKEN = "/refresh_token";
}
