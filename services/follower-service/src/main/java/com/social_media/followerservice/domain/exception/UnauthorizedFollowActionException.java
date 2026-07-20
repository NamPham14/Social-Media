package com.social_media.followerservice.domain.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

/**
 * Ném ra khi Feign Client nhận HTTP 401 từ service khác (User chưa xác thực).
 */
public class UnauthorizedFollowActionException extends BusinessRuleViolationException {
    public UnauthorizedFollowActionException() {
        super(4010, "Bạn không có quyền thực hiện hành động này trên dịch vụ từ xa");
    }
}
