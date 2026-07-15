# Create Post → Notification

## Kafka Topic

- **Name**: `post-created-topic`
- **Key**: `event.id` (String – UUID)
- **Value**: `PostCreatedIntegrationEvent` (JSON, serialized by `JsonSerializer`)
- **Producer Config**: `StringSerializer` (key), `JsonSerializer` (value), `acks=all`, idempotence enabled

## Event Class

**File**: `services/post-service/src/main/java/com/social_media/postservice/application/dto/events/PostCreatedIntegrationEvent.java`

##
@Value
public class PostCreatedIntegrationEvent {
    String id;          // UUID – unique event identifier
    String postId;      // ID của bài post vừa tạo (từ savedPost.getId().toString())
    String authorId;    // ID user tạo post (từ SecurityUtils.getCurrentUserId().toString())
    String caption;     // Nội dung caption của post
    LocalDateTime createdAt; // Thời điểm post được tạo
}

## Thời điểm gửi

Sau khi `postRepository.save(post)` thành công, trong `CreatePostUseCase` (`services/post-service/src/main/java/com/social_media/postservice/application/usecase/CreatePostUseCase.java`):

##
Post savedPost = postRepository.save(post);

PostCreatedIntegrationEvent event = new PostCreatedIntegrationEvent(
    UUID.randomUUID().toString(),
    savedPost.getId().toString(),
    SecurityUtils.getCurrentUserId().toString(),
    savedPost.getCaption(),
    savedPost.getCreatedAt()
);
postEventPublisher.publishPostCreated(event);


## Ví dụ JSON gửi lên Kafka

##
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "postId": "42",
  "authorId": "15",
  "caption": "Ảnh đẹp quá trời!",
  "createdAt": "2026-06-29T10:30:00"
}


## Lưu ý cho notification-service

- Các field `postId`, `authorId` đang là `String` (vì `savedPost.getId()` và `SecurityUtils.getCurrentUserId()` trả về `Long`, được convert `.toString()`). Bên consumer cần parse lại thành `Long` nếu Notification entity dùng kiểu `Long`.
- `id` là UUID – dùng làm `sourceEventId` để chống duplicate notification khi Kafka gửi lại message.
- Để tạo notification cho **followers của author**, cần:
  - `actorId` = `authorId` (15)
  - `recipientId` = từng followerId (cần gọi sang follower-service hoặc tự lookup)
  - `targetType` = `POST`
  - `targetId` = `postId` (42)
  - `notificationType` = ? (hiện tại chưa có `POST_CREATED` trong enum `NotificationType`, cần thêm nếu muốn)
- **Idempotency**: DB có unique constraint `uk_notifications_source_event_id` trên `source_event_id`.

## Class gửi

**File**: `services/post-service/src/main/java/com/social_media/postservice/infrastructure/mesaging/kafka/publisher/KafkaPostEventPublisher.java`

##
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostEventPublisher implements PostEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String TOPIC = "post-created-topic";

    @Override
    public void publishPostCreated(PostCreatedIntegrationEvent event) {
        kafkaTemplate.send(TOPIC, event.getId()) // key = event.id
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("Post Created Event Send Success: {}", event.getId());
                    } else {
                        log.error("Post Created Event Send Failed: {}", ex.getMessage());
                    }
                });
    }
}
