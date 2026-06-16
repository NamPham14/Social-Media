package com.social_media.identityservice.application.usecase;

import com.social_media.identityservice.application.command.RegisterCommand;
import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import com.social_media.identityservice.domain.model.role.aggregate.Role;
import com.social_media.identityservice.domain.repository.RoleRepository;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.application.exception.user.UserExistedException;
import com.social_media.identityservice.infrastructure.client.ProfileClient;
import com.social_media.identityservice.api.dto.request.ProfileCreationRequest;
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
        if (userRepository.existsByEmail(command.email())) {
            throw new UserExistedException();
        }
        if (userRepository.existsByUsername(command.username())) {
            throw new UserExistedException();
        }

        RoleId userRoleId = RoleId.from("USER");
        Role userRole = roleRepository.findById(userRoleId)
                .orElseGet(() -> roleRepository.save(Role.create("USER", "Default user role")));

        User user = User.register(
                command.username(),
                command.email(),
                passwordEncoder.encode(command.password()),
                Set.of(userRoleId)
        );

        User savedUser = userRepository.save(user);

        // Gọi sang Profile Service để tạo profile tương ứng (Sẽ chuyển sang Event sau)
        profileClient.createProfile(ProfileCreationRequest.builder()
                .id(savedUser.getId().value())
                .username(savedUser.getUsername())
                .fullName(savedUser.getUsername())
                .build());

        return savedUser;
    }
}
