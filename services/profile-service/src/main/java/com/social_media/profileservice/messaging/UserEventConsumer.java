package com.social_media.profileservice.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.profileservice.application.command.CreateProfileCommand;
import com.social_media.profileservice.application.usecase.CreateProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {
    private final ObjectMapper objectMapper;
    private final CreateProfileUseCase createProfileUseCase;

    // Túc trực 24/7 ở trạm "user-events"
    @KafkaListener(topics = "user-events",groupId = "profile-group")
    public void consumeUserEvent(String message){
        log.info("======>Bắt được một lá thư từ Kafka: {}", message);
        try{
            //Mở thư (phân tích JSON)
            JsonNode payload = objectMapper.readTree(message);

            // bóc tác thông tin
            if(payload.has("userId")){
                UUID userId = UUID.fromString(payload.get("userId").asText());
                String username = payload.get("fullName").asText();

                // Tạo Profile trống ngay lập tức (Lấy tên username làm Tên hiển thị luôn)
                CreateProfileCommand command = new CreateProfileCommand(userId, username, username);
                createProfileUseCase.execute(command);

                log.info("==>>Đã tạo thành công Profile trống cho user: {}", username);

            }
        }catch (Exception e){
            log.error("====>Xử lý lá thư thất bại!", e);
        }
    }




}
