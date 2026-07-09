package com.social_media.identityservice.domain.model.user.service;

public interface DomainPasswordEncoder {
    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
