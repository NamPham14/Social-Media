package com.social_media.postservice.infrastructure.cilent.config;

import com.social_media.postservice.infrastructure.cilent.decoder.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


public class FeignClientConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                // 1. Connect Timeout: Thời gian tối đa để kết nối đến identity-service
                .connectTimeout(3, TimeUnit.SECONDS)

                // 2. Read/Socket Timeout: Thời gian tối đa chờ identity-service xử lý và trả data về
                .readTimeout(5, TimeUnit.SECONDS)

                // 3. Write Timeout (Mục 4.3): Thời gian tối đa để đẩy dữ liệu lớn (như upload file, JSON nặng) lên server
                .writeTimeout(5, TimeUnit.SECONDS)

                .build();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

}
