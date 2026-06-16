package com.social_media.identityservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.identityservice.api.dto.request.LoginRequest;
import com.social_media.identityservice.api.dto.request.RegisterRequest;
import com.social_media.identityservice.api.dto.response.LoginResponse;
import com.social_media.identityservice.api.dto.response.UserResponse;
import com.social_media.identityservice.application.command.LoginCommand;
import com.social_media.identityservice.application.command.RegisterCommand;
import com.social_media.identityservice.application.mapper.IdentityApiMapper;
import com.social_media.identityservice.application.usecase.LoginUseCase;
import com.social_media.identityservice.application.usecase.RegisterUseCase;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class UserController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final IdentityApiMapper identityMapper;

    @PostMapping(ApiPath.USERS + ApiPath.REGISTER)
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        RegisterCommand command = RegisterCommand.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();

        User user = registerUseCase.register(command);
        UserResponse response = identityMapper.toResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<UserResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .code(1000)
                        .message("User registered successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping(ApiPath.AUTH + ApiPath.LOGIN)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginCommand command = LoginCommand.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        LoginResponse response = loginUseCase.login(command);

        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Login successfully")
                .data(response)
                .build());
    }
}
