package com.social_media.identityservice.infrastructure.client;


import com.social_media.common.exception.ServiceUnavailableException;
import com.social_media.identityservice.application.exception.user.UserNotFoundException;
import com.social_media.identityservice.infrastructure.exception.ProfileServiceDownException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return  new CustomerErrorDecoder();
    }

    public static class CustomerErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            if(response.status() == 404) {
                return new UserNotFoundException();
            }

            if(response.status() == 500) {
                return new ProfileServiceDownException("Profile Service is currently down!");
            }

            return new ServiceUnavailableException("Error occurred while communicating with other services");
        }
    }

}
