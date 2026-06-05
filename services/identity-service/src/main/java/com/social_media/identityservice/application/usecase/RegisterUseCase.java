package com.social_media.identityservice.application.usecase;

import com.social_media.common.exception.AppException;
import com.social_media.common.exception.ErrorCode;
import com.social_media.identityservice.api.dto.ProfileCreationRequest;
import com.social_media.identityservice.application.command.RegisterCommand;
import com.social_media.identityservice.domain.Role;
import com.social_media.identityservice.domain.RoleRepository;
import com.social_media.identityservice.domain.User;
import com.social_media.identityservice.domain.UserRepository;
import com.social_media.identityservice.infrastructure.client.ProfileClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileClient profileClient;

    @Transactional
    public User register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        User user = User.builder()
                .username(command.getUsername())
                .password(passwordEncoder.encode(command.getPassword()))
                .email(command.getEmail())
                .roles(Set.of(userRole))
                .build();

        User savedUser = userRepository.save(user);

        // Gọi sang Profile Service để tạo profile tương ứng
        profileClient.createProfile(ProfileCreationRequest.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getUsername()) // Tạm thời lấy username làm fullName
                .build());

        return savedUser;
    }
}
