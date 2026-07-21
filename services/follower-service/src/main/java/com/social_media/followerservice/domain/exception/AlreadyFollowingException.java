package com.social_media.followerservice.domain.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

/**
 * Ném ra khi User đã follow người này rồi (không thể follow 2 lần).
 */
public class AlreadyFollowingException extends BusinessRuleViolationException {
    public AlreadyFollowingException() {
        super(4090, "Bạn đã theo dõi người dùng này rồi");
    }
}
