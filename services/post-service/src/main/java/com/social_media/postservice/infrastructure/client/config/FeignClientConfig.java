package com.social_media.postservice.infrastructure.client.config;

import com.social_media.postservice.infrastructure.client.decoder.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;


public class FeignClientConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)

                .build();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

}
