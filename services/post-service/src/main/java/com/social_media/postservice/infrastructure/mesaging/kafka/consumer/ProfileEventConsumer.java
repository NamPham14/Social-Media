package com.social_media.postservice.infrastructure.mesaging.kafka.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.postservice.infrastructure.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileEventConsumer {

    private final ObjectMapper objectMapper;
    private final PostJpaRepository postJpaRepository; // Dùng trực tiếp JPA để chạy lệnh Update cho lẹ


    @KafkaListener(topics = "profile-events", groupId = "post-group")
    @Transactional
    public void consumeProfileEvent(String message) {
        log.info(" [POST-SERVICE] Nhận tin nhắn đổi Profile: {}", message);
        try {
            JsonNode payload = objectMapper.readTree(message);

            // Bóc tách thông tin
            UUID userId = UUID.fromString(payload.get("userId").asText());
            String newName = payload.has("authorName") && !payload.get("authorName").isNull() ? payload.get("authorName").asText() : "Unknown";
            String newAvatar = payload.has("authorAvatarUrl") && !payload.get("authorAvatarUrl").isNull() ? payload.get("authorAvatarUrl").asText() : null;

            // Xài tuyệt chiêu SQL Native (Hoặc JPQL) quét sạch toàn bộ DB và đổi tên trong chớp mắt
            postJpaRepository.updateAuthorInfo(userId, newName, newAvatar);

            log.info("Đã cập nhật thành công Tên/Avatar mới cho toàn bộ bài viết của User: {}", userId);
        } catch (Exception e) {
            log.error("Xử lý tin nhắn Profile thất bại!", e);
        }
    }
}
