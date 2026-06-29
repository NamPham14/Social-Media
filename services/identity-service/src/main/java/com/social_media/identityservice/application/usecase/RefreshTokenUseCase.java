package com.social_media.identityservice.application.usecase;

import com.social_media.common.exception.BusinessRuleViolationException;


import com.social_media.identityservice.api.dto.response.TokenRefreshResponse;
import com.social_media.identityservice.application.command.TokenRefreshCommand;
import com.social_media.identityservice.application.exception.user.UserNotFoundException;
import com.social_media.identityservice.config.JwtProvider;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private  final StringRedisTemplate redisTemplate;
    private  final JwtProvider jwtProvider;
    private  final UserRepository userRepository;


    public TokenRefreshResponse refreshToken(TokenRefreshCommand command) {

        String userIdStr = redisTemplate.opsForValue().get("refresh_token:" + command.refreshToken());

        if(userIdStr == null){
            throw new BusinessRuleViolationException(1005, "Refresh Token không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findById(new UserId(UUID.fromString(userIdStr)))
                .orElseThrow(() -> new UserNotFoundException());

        String newAccessToken = jwtProvider.generateToken(userIdStr,user.getUsername());

        return  new TokenRefreshResponse(newAccessToken,command.refreshToken());
    }



}
