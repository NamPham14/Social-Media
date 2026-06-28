package com.social_media.identityservice.application.usecase;

import com.social_media.identityservice.application.command.LogoutCommand;
import com.social_media.identityservice.config.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final StringRedisTemplate redisTemplate;
    private final JwtProvider jwtProvider;

    public  void logout(LogoutCommand command){
        // xoa refreshToken
        redisTemplate.delete("refresh_token:" +command.refreshToken());

        //thu access token
        long remainingTime = jwtProvider.getRemainingTime(command.accessToken());

        if(remainingTime > 0){
            redisTemplate.opsForValue().set(
                    "blacklist:" + command.accessToken(),
                    "revoked",   Duration.ofMillis(remainingTime)   );
        }


    }
}
