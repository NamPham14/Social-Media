package com.social_media.identityservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.identityservice.api.dto.response.ProfileView;
import com.social_media.identityservice.api.dto.response.UserResponse;
import com.social_media.identityservice.application.mapper.IdentityApiMapper;
import com.social_media.identityservice.application.usecase.BanUserUseCase;
import com.social_media.identityservice.application.usecase.FindAllUsersUseCase;
import com.social_media.identityservice.application.usecase.GetUserStatusUseCase;
import com.social_media.identityservice.application.usecase.UnbanUserUseCase;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import com.social_media.identityservice.infrastructure.client.ProfileClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class AdminUserController {
    private final BanUserUseCase banUserUseCase;
    private final GetUserStatusUseCase getUserStatusUseCase;
    private final ProfileClient profileClient;
    private final UnbanUserUseCase unbanUserUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;
    private final IdentityApiMapper identityApiMapper;

    @PostMapping("/{userId}/ban")
    public ResponseEntity<String> banUser(@PathVariable("userId") UUID userId) {
        banUserUseCase.banUser(UserId.from(userId));
        return ResponseEntity.ok("User has been banned");
    }

    // API nội bộ dành riêng cho Post-Service gọi sang bằng FeignClient
    @GetMapping("/{userId}/status")
    public ResponseEntity<String> getUserStatus(@PathVariable("userId") UUID userId) {
        String status = getUserStatusUseCase.getStatus(UserId.from(userId));
        return ResponseEntity.ok(status);
    }
    @PostMapping("/{userId}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable("userId") UUID userId) {
        unbanUserUseCase.unbanUser(UserId.from(userId));
        return ResponseEntity.ok("User has been unbanned successfully");
    }

    @GetMapping("/{userId}/detail")
    public ResponseEntity<ApiResponse<Object>> getUserDetail(@PathVariable("userId") UUID userId) {

        String status = getUserStatusUseCase.getStatus(UserId.from(userId));

        ApiResponse<ProfileView> profileData = profileClient.getProfile(userId);

        return ResponseEntity.ok(ApiResponse.success(
                new Object(){
                    public final String accountStatus = status;
                    public final ProfileView profileView = profileData.getData();
                },
                "Lấy thông tin tổng hợp thành công"
        ));
    }


    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
                                                                        @RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(required = false) String keyword) {

        // Gọi UseCase lấy Page<User>
        Page<User> userPage = findAllUsersUseCase.execute(page, size,keyword);

        // Dùng hàm map() của Page để ép cục Domain User thành DTO UserResponse
        Page<UserResponse> responsePage = userPage.map(identityApiMapper::toResponse);

        return ResponseEntity.ok(ApiResponse.success(responsePage, "Lấy danh sách người dùng thành công"));
    }
}
