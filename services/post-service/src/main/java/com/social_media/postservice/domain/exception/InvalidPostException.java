package com.social_media.postservice.domain.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class InvalidPostException extends BusinessRuleViolationException {
    public InvalidPostException(String message) {
        super(4000, message); // 4000 là mã lỗi chung cho dữ liệu/trạng thái không hợp lệ của Post
    }
}
