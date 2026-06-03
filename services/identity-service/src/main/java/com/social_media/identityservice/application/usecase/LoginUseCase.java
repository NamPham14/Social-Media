package com.social_media.identityservice.application.usecase;


import com.social_media.common.exception.AppException;
import com.social_media.common.exception.ErrorCode;
import com.social_media.commonsecurity.jwt.JwtProvider;
import com.social_media.identityservice.api.dto.LoginResponse;
import com.social_media.identityservice.application.command.LoginCommand;
import com.social_media.identityservice.domain.User;
import com.social_media.identityservice.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginCommand command) {

        User user = userRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));


        boolean authenticated = passwordEncoder.matches(command.getPassword(), user.getPassword());
        
        if (!authenticated) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }


        String token = jwtProvider.generateToken(user.getUsername());


        return new LoginResponse(token, true);
    }
}
