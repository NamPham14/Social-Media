package com.social_media.identityservice.application.usecase;


import com.social_media.common.exception.AppException;
import com.social_media.common.exception.ErrorCode;
import com.social_media.identityservice.api.dto.UserResponse;
import com.social_media.identityservice.application.command.RegisterCommand;
import com.social_media.identityservice.domain.User;
import com.social_media.identityservice.domain.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterCommand command){
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if(userRepository.existsByUsername(command.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = User.builder()
                .username(command.getUsername())
                .password(passwordEncoder.encode(command.getPassword()))
                .email(command.getEmail())
                .roles(Set.of("USER"))
                .build();

        return userRepository.save(user);
    }
}
