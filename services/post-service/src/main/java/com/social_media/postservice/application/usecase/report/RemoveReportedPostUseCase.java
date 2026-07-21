package com.social_media.postservice.application.usecase.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.postservice.application.command.ReviewReportCommand;
import com.social_media.postservice.application.dto.events.PostDeleteIntegrationEvent;
import com.social_media.postservice.application.exception.ResourceNotFoundException;
import com.social_media.postservice.application.ports.output.PostEventPublisher;
import com.social_media.postservice.application.service.MediaService;
import com.social_media.postservice.config.security.SecurityUtils;
import com.social_media.postservice.domain.model.outbox.OutBox;
import com.social_media.postservice.domain.model.outbox.OutboxStatus;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.model.post.valueobject.PostMedia;
import com.social_media.postservice.domain.model.report.aggregate.Report;
import com.social_media.postservice.domain.repository.OutBoxRepository;
import com.social_media.postservice.domain.repository.PostRepository;
import com.social_media.postservice.domain.model.report.valueobject.ReportStatus;
import com.social_media.postservice.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoveReportedPostUseCase {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final MediaService mediaService;
    private final PostEventPublisher postEventPublisher;
    private final ObjectMapper objectMapper;
    private final OutBoxRepository outBoxRepository;

    @Transactional
    public void execute(ReviewReportCommand command) {
        Report report = reportRepository.findById(command.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException());

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new com.social_media.postservice.application.exception.ReportAlreadyProcessedException();
        }

        Post post = postRepository.findById(report.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException());

        post.removeByAdmin(command.getAdminId());
        report.actOn();

        postRepository.save(post);
        reportRepository.save(report);

        List<PostMedia> postMediaList = new ArrayList<>(post.getMedias());
        for (PostMedia postMedia : postMediaList) {
            try {
                mediaService.deleteFile(postMedia.getPublicId());
            } catch (Exception e) {
                log.error("Remove reported post thành công nhưng chưa xóa được ảnh trên Cloudinary cho publicId: "
                        + postMedia.getPublicId(), e);
            }
        }

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

//        postEventPublisher.publishPostDelete(new PostDeleteIntegrationEvent(
//                UUID.randomUUID().toString(),
//                post.getId().toString(),
//                post.getUserId().toString()
//        ));
    }
}
