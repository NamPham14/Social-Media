package com.social_media.identityservice.api.dto.request;


import lombok.Data;

@Data

public class TokenRefreshRequest {
    private String refreshToken;
}
