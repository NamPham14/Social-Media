package com.social_media.identityservice.application.command;


import lombok.Builder;

@Builder
public record LoginCommand(String username, String password) {}
