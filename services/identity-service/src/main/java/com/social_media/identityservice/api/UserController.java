package com.social_media.identityservice.api;


import com.social_media.common.api.ApiResponse;
import com.social_media.identityservice.api.dto.LoginRequest;
import com.social_media.identityservice.api.dto.LoginResponse;
import com.social_media.identityservice.api.dto.RegisterRequest;
import com.social_media.identityservice.api.dto.UserResponse;
import com.social_media.identityservice.application.command.LoginCommand;
import com.social_media.identityservice.application.command.RegisterCommand;
import com.social_media.identityservice.application.usecase.LoginUseCase;
import com.social_media.identityservice.application.usecase.RegisterUseCase;
import com.social_media.identityservice.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class UserController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;


    @PostMapping(ApiPath.USERS + ApiPath.REGISTER)
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody RegisterRequest request) {

        RegisterCommand registerCommand = new RegisterCommand(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        User user = registerUseCase.register(registerCommand);

        // Map Entity sang DTO để trả về
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(HttpStatus.OK.value())
                        .code(1000)
                        .message("User registered successfully")
                        .data(response)
                        .build()
        );

    }

    @PostMapping(ApiPath.AUTH + ApiPath.LOGIN)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.getUsername(), request.getPassword());

        LoginResponse response = loginUseCase.login(command);
        return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                        .status(HttpStatus.OK.value())
                        .code(1000)
                        .message("Login successfully")
                        .data(response)
                        .build()
        );

    }
}