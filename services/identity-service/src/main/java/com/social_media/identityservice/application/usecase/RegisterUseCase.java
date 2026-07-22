package com.social_media.identityservice.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.social_media.identityservice.application.command.RegisterCommand;
import com.social_media.identityservice.domain.repository.OutboxEventRepository;
import com.social_media.identityservice.domain.shared.valueobject.RoleId;
import com.social_media.identityservice.domain.model.role.aggregate.Role;
import com.social_media.identityservice.domain.repository.RoleRepository;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.application.exception.user.UserExistedException;
import com.social_media.identityservice.api.dto.request.ProfileCreationRequest;
import com.social_media.identityservice.infrastructure.client.ProfileServiceHelper;
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
    private final ProfileServiceHelper profileServiceHelper;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public User register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserExistedException();
        }
        if (userRepository.existsByUsername(command.username())) {
            throw new UserExistedException();
        }

        RoleId userRoleId = RoleId.from("USER");
        roleRepository.findById(userRoleId)
                .orElseGet(() -> roleRepository.save(Role.create("USER", "Default user role")));

        User user = User.register(
                command.username(),
                command.email(),
                passwordEncoder.encode(command.password()),
                Set.of(userRoleId)
        );

        User savedUser = userRepository.save(user);

        // Gọi sang Profile Service để tạo profile tương ứng (Sẽ chuyển sang Event sau)
//        profileServiceHelper.createSafeProfile(ProfileCreationRequest.builder()
//                .id(savedUser.getId().value())
//                .username(savedUser.getUsername())
//                .fullName(savedUser.getUsername())
//                .build());

        // ---------------- OUTBOX PATTERN ----------------
        // Bước 1: Đóng gói thông tin User mới thành chuỗi JSON (Payload)
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("userId",savedUser.getId().value().toString());
        payload.put("username", savedUser.getUsername());
        payload.put("fullName", savedUser.getUsername());
        payload.put("email", savedUser.getEmail());

        // Bước 2: Lưu bức thư vào Outbox (Bảng outbox_events)
        // Vì hành động này nằm trong hàm @Transactional, nó sẽ được lưu CÙNG LÚC với lệnh save(user) ở dòng trên.
        outboxEventRepository.save(savedUser.getId().value().toString(),
                "USER_REGISTERED",
                payload.toString());

        return savedUser;
    }
}
