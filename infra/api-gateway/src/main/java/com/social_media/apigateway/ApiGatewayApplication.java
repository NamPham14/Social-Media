package com.social_media.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {
                "com.social_media.apigateway",
                "com.social_media.commonsecurity.jwt"
        },
        exclude = {
                DataSourceAutoConfiguration.class,
                SecurityAutoConfiguration.class,
                ReactiveSecurityAutoConfiguration.class,
                ReactiveManagementWebSecurityAutoConfiguration.class
        }
)
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
