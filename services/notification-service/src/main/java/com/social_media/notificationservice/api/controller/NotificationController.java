package com.social_media.notificationservice.api.controller;

import com.social_media.common.api.ApiResponse;
import com.social_media.notificationservice.api.dto.response.NotificationResponse;
import com.social_media.notificationservice.api.path.ApiPath;
import com.social_media.notificationservice.application.usecase.CountUnreadNotificationsUseCase;
import com.social_media.notificationservice.application.usecase.DeleteNotificationUseCase;
import com.social_media.notificationservice.application.usecase.GetNotificationsUseCase;
import com.social_media.notificationservice.application.usecase.GetUnreadNotificationsUseCase;
import com.social_media.notificationservice.application.usecase.MarkAllNotificationsAsReadUseCase;
import com.social_media.notificationservice.application.usecase.MarkNotificationAsReadUseCase;
import com.social_media.notificationservice.application.usecase.MarkNotificationAsUnreadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE)
@RequiredArgsConstructor
public class NotificationController {

    private final GetNotificationsUseCase getNotificationsUseCase;
    private final GetUnreadNotificationsUseCase getUnreadNotificationsUseCase;
    private final CountUnreadNotificationsUseCase countUnreadNotificationsUseCase;
    private final MarkNotificationAsReadUseCase markNotificationAsReadUseCase;
    private final MarkAllNotificationsAsReadUseCase markAllNotificationsAsReadUseCase;
    private final MarkNotificationAsUnreadUseCase markNotificationAsUnreadUseCase;
    private final DeleteNotificationUseCase deleteNotificationUseCase;

    @GetMapping(ApiPath.MY_NOTIFICATIONS)
    public ApiResponse<List<NotificationResponse>> getMyNotifications(
            @RequestHeader("X-Auth-User-Id") String currentUserId,
            @RequestParam(name = "limit", defaultValue = "20") int limit
    ) {
        return ApiResponse.success(
                getNotificationsUseCase.execute(currentUserId, limit),
                "Get notifications success"
        );
    }

    @GetMapping(ApiPath.MY_UNREAD_NOTIFICATIONS)
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications(
            @RequestHeader("X-Auth-User-Id") String currentUserId,
            @RequestParam(name = "limit", defaultValue = "20") int limit
    ) {
        return ApiResponse.success(
                getUnreadNotificationsUseCase.execute(currentUserId, limit),
                "Get unread notifications success"
        );
    }

    @GetMapping(ApiPath.MY_UNREAD_COUNT)
    public ApiResponse<Long> countUnread(
            @RequestHeader("X-Auth-User-Id") String currentUserId
    ) {
        return ApiResponse.success(
                countUnreadNotificationsUseCase.execute(currentUserId),
                "Count unread notifications success"
        );
    }

    @PatchMapping(ApiPath.MARK_ALL_AS_READ)
    public ApiResponse<Void> markAllAsRead(
            @RequestHeader("X-Auth-User-Id") String currentUserId
    ) {
        markAllNotificationsAsReadUseCase.execute(currentUserId);
        return ApiResponse.success("Mark all notifications as read success");
    }

    @PatchMapping(ApiPath.MARK_AS_READ)
    public ApiResponse<Void> markAsRead(
            @RequestHeader("X-Auth-User-Id") String currentUserId,
            @PathVariable("notificationId") Long notificationId
    ) {
        markNotificationAsReadUseCase.execute(notificationId, currentUserId);
        return ApiResponse.success("Mark notification as read success");
    }

    @PatchMapping(ApiPath.MARK_AS_UNREAD)
    public ApiResponse<Void> markAsUnread(
            @RequestHeader("X-Auth-User-Id") String currentUserId,
            @PathVariable("notificationId") Long notificationId
    ) {
        markNotificationAsUnreadUseCase.execute(notificationId, currentUserId);
        return ApiResponse.success("Mark notification as unread success");
    }

    @DeleteMapping(ApiPath.DELETE)
    public ApiResponse<Void> delete(
            @RequestHeader("X-Auth-User-Id") String currentUserId,
            @PathVariable("notificationId") Long notificationId
    ) {
        deleteNotificationUseCase.execute(notificationId, currentUserId);
        return ApiResponse.success("Delete notification success");
    }
}

