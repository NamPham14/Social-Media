package com.social_media.common.exception;

public class ServiceUnavailableException extends DomainException {
    public ServiceUnavailableException(String serviceName) {
        super(String.format("Dịch vụ %s hiện không phản hồi. Vui lòng thử lại sau.", serviceName));
    }
}
