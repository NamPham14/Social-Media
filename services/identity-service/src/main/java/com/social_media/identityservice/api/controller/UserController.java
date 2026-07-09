package com.social_media.identityservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.identityservice.api.dto.request.ChangePasswordRequest;
import com.social_media.identityservice.api.dto.response.ChangePasswordResponse;
import com.social_media.identityservice.api.dto.response.UserResponse;
import com.social_media.identityservice.application.command.ChangePasswordCommand;
import com.social_media.identityservice.application.usecase.ChangePasswordUseCase;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class UserController {

    private final ChangePasswordUseCase changePasswordUseCase;


    @PutMapping("/change-pwd/{userId}")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>>  changePassword (@RequestBody ChangePasswordRequest changePasswordRequest,
                                                                @PathVariable("userId") UUID userId){

        ChangePasswordCommand command = ChangePasswordCommand.builder()
                .newPassword(changePasswordRequest.getNewPassword())
                .oldPassword(changePasswordRequest.getOldPassword())
                .build();

        changePasswordUseCase.execute(UserId.from(userId),command);
        ChangePasswordResponse response = new ChangePasswordResponse(true, "Password changed!");

        return ResponseEntity.ok(ApiResponse.success(response, "Change Password successfully"));

    }



}
