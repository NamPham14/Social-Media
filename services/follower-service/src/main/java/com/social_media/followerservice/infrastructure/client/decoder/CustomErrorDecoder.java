package com.social_media.followerservice.infrastructure.client.decoder;

import com.social_media.common.exception.BusinessRuleViolationException;
import com.social_media.common.exception.EntityNotFoundException;
import com.social_media.common.exception.ServiceUnavailableException;
import com.social_media.followerservice.domain.exception.UnauthorizedActionException;
import com.social_media.followerservice.domain.exception.UnauthorizedFollowActionException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                return new BusinessRuleViolationException(4000, "Dữ liệu yêu cầu không hợp lệ");
            case 401:
                return new UnauthorizedActionException();
            case 403:
                return new UnauthorizedFollowActionException();
            case 404:
                return new EntityNotFoundException("Target user or article not found from remote service");
            case 503:
                return new ServiceUnavailableException("Remote service is currently unavailable");
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
