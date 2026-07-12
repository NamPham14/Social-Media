package com.social_media.followerservice.infrastructure.client.decoder;

import com.social_media.common.exception.BusinessRuleViolationException;
import com.social_media.common.exception.EntityNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 404:
                return new EntityNotFoundException("Target user or article not found from remote service");
            case 400:
                return new BusinessRuleViolationException(4000, "Dữ liệu yêu cầu gửi đi không hợp lệ");
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
