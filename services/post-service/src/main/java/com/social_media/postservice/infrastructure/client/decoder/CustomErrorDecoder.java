package com.social_media.postservice.infrastructure.client.decoder;

import com.social_media.common.exception.AppException;
import com.social_media.common.exception.ErrorCode;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {
            case 404:
                return new AppException(ErrorCode.USER_NOT_FOUND);
            case 401:
                return new AppException(ErrorCode.USER_BANNED);
            case 400:
                return new AppException(ErrorCode.INVALID_KEY);
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }


}
