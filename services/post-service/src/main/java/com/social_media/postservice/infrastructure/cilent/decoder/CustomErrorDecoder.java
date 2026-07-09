package com.social_media.postservice.infrastructure.cilent.decoder;

import com.social_media.postservice.application.exception.ResourceNotFoundException;
import com.social_media.postservice.application.exception.UnauthorizedActionException;
import com.social_media.common.exception.BusinessRuleViolationException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {
            case 404:
                return new ResourceNotFoundException();
            case 401:
                return new UnauthorizedActionException();
            case 400:
                return new BusinessRuleViolationException(4000, "Dữ liệu yêu cầu không hợp lệ");
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
