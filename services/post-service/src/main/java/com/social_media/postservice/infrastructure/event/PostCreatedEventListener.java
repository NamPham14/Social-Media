package com.social_media.postservice.infrastructure.event;


import com.social_media.postservice.application.service.PostModerationService;
import com.social_media.postservice.domain.model.post.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCreatedEventListener {

    private final PostModerationService postModerationService;

    @Async("moderationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPostCreated(PostCreatedEvent event) {
        log.info("Bắt đầu kiểm duyệt ngầm cho post: {}", event.postId());
        postModerationService.moderate(event.postId(), event.caption());
    }
}
