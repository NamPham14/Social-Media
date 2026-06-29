package com.social_media.identityservice.application.usecase;

import com.social_media.identityservice.config.JwtProvider;
import com.social_media.identityservice.api.dto.response.LoginResponse;
import com.social_media.identityservice.application.command.LoginCommand;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.application.exception.user.UserNotFoundException;
import com.social_media.identityservice.application.exception.user.UnauthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    public LoginResponse login(LoginCommand command) {
        User user = userRepository.findByUsername(command.username())
                .orElseThrow(UserNotFoundException::new);

        boolean authenticated = passwordEncoder.matches(command.password(), user.getPassword());
        String userIdStr = user.getId().getValue().toString();
        
        if (!authenticated) {
            throw new UnauthenticatedException();
        }

        String accessToken = jwtProvider.generateToken(userIdStr,user.getUsername());

        String refreshToken =  UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                "refresh_token:" + refreshToken,
                userIdStr,
                Duration.ofDays(7)
        );
        return new LoginResponse(accessToken,refreshToken, true);
    }
}
