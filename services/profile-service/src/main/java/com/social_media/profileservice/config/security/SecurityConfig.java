package com.social_media.profileservice.config.security;


import com.social_media.commonsecurity.filter.GatewayHeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Nếu có các API nào không cần đăng nhập (như xem public profile) thì bạn thêm vào đây:
                        .requestMatchers("/api/v1/profiles/public/**").permitAll()
                        // Còn lại tất cả các API khác đều phải bắt buộc có "X-Auth-User-Id" từ Gateway ném xuống
                        .anyRequest().authenticated()
                )
                // KÍCH HOẠT KHIÊN BẢO VỆ CHUNG
                .addFilterBefore(new GatewayHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
