package com.social_media.identityservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.identityservice.api.dto.request.LoginRequest;
import com.social_media.identityservice.api.dto.request.LogoutRequest;
import com.social_media.identityservice.api.dto.request.RegisterRequest;
import com.social_media.identityservice.api.dto.request.TokenRefreshRequest;
import com.social_media.identityservice.api.dto.response.LoginResponse;
import com.social_media.identityservice.api.dto.response.TokenRefreshResponse;
import com.social_media.identityservice.api.dto.response.UserResponse;
import com.social_media.identityservice.application.command.LoginCommand;
import com.social_media.identityservice.application.command.LogoutCommand;
import com.social_media.identityservice.application.command.RegisterCommand;
import com.social_media.identityservice.application.command.TokenRefreshCommand;
import com.social_media.identityservice.application.mapper.IdentityApiMapper;
import com.social_media.identityservice.application.usecase.*;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final IdentityApiMapper identityMapper;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;


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

    @PostMapping(ApiPath.AUTH + ApiPath.REFRESH_TOKEN)
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshCommand command = TokenRefreshCommand.builder()
                .refreshToken(request.getRefreshToken())
                .build();
        TokenRefreshResponse response = refreshTokenUseCase.refreshToken(command);

        return ResponseEntity.ok(ApiResponse.<TokenRefreshResponse>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Token refreshed successfully")
                .data(response)
                .build());
    }

    @PostMapping(ApiPath.AUTH + "/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
        LogoutCommand command = LogoutCommand.builder()
                .accessToken(request.getAccessToken())
                .refreshToken(request.getRefreshToken())
                .build();

        logoutUseCase.logout(command);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Logout successfully")
                .build());
    }

}
