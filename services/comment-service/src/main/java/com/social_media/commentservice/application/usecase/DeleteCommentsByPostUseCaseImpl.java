package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.application.event.PostCommentsDeletedEvent;
import com.social_media.commentservice.application.event.PostDeletedEvent;
import com.social_media.commentservice.application.port.out.CommentEventOutbox;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCommentsByPostUseCaseImpl implements DeleteCommentsByPostUseCase {

    private static final int EVENT_BATCH_SIZE = 500;

    private final CommentRepository commentRepository;
    private final CommentEventOutbox outbox;

    @Override
    @Transactional
    public int execute(PostDeletedEvent event) {
        List<UUID> commentIds = commentRepository.findActiveIdsByPostId(event.postId());
        int deleted = commentRepository.softDeleteAllByPostId(event.postId());

        for (int start = 0; start < commentIds.size(); start += EVENT_BATCH_SIZE) {
            int end = Math.min(start + EVENT_BATCH_SIZE, commentIds.size());
            outbox.append(new PostCommentsDeletedEvent(
                    UUID.randomUUID().toString(),
                    event.postId(),
                    commentIds.subList(start, end)
            ));
        }
        return deleted;
    }
}
