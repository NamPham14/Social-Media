package com.social_media.postservice.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.postservice.application.command.DeletePostCommand;
import com.social_media.postservice.application.dto.events.PostDeleteIntegrationEvent;
import com.social_media.postservice.application.ports.output.PostEventPublisher;
import com.social_media.postservice.application.service.MediaService;
import com.social_media.postservice.config.security.SecurityUtils;
import com.social_media.postservice.domain.model.outbox.OutBox;
import com.social_media.postservice.domain.model.outbox.OutboxStatus;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.model.post.entity.PostMedia;
import com.social_media.postservice.domain.repository.BookmarkRepository;
import com.social_media.postservice.domain.repository.OutBoxRepository;
import com.social_media.postservice.domain.repository.PostRepository;
import com.social_media.postservice.domain.repository.ReportRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class DeletePostUseCase {

    private final PostRepository postRepository;
    private final MediaService mediaService;
    private final PostEventPublisher postEventPublisher;
    private final BookmarkRepository bookmarkRepository;
    private final ReportRepository reportRepository;
    private final OutBoxRepository outBoxRepository;
    private final ObjectMapper objectMapper;

    public void execute(DeletePostCommand command) {
        Post post = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new com.social_media.postservice.application.exception.ResourceNotFoundException());

        if (!post.getUserId().equals(command.getUserId())) {
            throw new com.social_media.postservice.application.exception.UnauthorizedActionException();
        }

        post.softDelete();
        postRepository.save(post);

        List<PostMedia> postMediaList = new ArrayList<>(post.getMedias());
        for (PostMedia postMedia : postMediaList) {
            try {
                mediaService.deleteFile(postMedia.getPublicId());
            } catch (Exception e) {
                log.error("SoftDelete thành công nhưng chưa xóa được ảnh trên Cloudinary cho publicId: "
                        + postMedia.getPublicId(), e);
            }
        }

        bookmarkRepository.deleteAllByPostId(post.getId());
        reportRepository.deleteReportByPostId(post.getId());

        PostDeleteIntegrationEvent postDeleteIntegrationEvent= new PostDeleteIntegrationEvent(
                UUID.randomUUID().toString(),
                post.getId().toString(),
                SecurityUtils.getCurrentUserId().toString()
        );

        try{
            OutBox outBox = OutBox.builder()
                    .id(UUID.randomUUID())
                    .topic("post-delete")
                    .eventType("DELETE POST")
                    .payload(objectMapper.writeValueAsString(postDeleteIntegrationEvent))
                    .status(OutboxStatus.NEW)
                    .createdAt(LocalDateTime.now())
                    .build();

            outBoxRepository.save(outBox);
        }catch (Exception e){
            log.info("Không lưu đc outbox:...");
        }


//        postEventPublisher.publishPostDelete(postDeleteIntegrationEvent);





    }
}

