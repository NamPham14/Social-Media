package com.social_media.postservice.application.usecase.post;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.postservice.application.exception.CloudinaryUploadException;
import com.social_media.postservice.application.exception.UnauthorizedActionException;
import com.social_media.postservice.application.command.CreatePostCommand;
import com.social_media.postservice.application.dto.PostResponse;
import com.social_media.postservice.application.dto.UploadResponse;
import com.social_media.postservice.application.dto.events.PostCreatedIntegrationEvent;
import com.social_media.postservice.application.ports.output.PostEventPublisher;
import com.social_media.postservice.config.security.SecurityUtils;
import com.social_media.postservice.domain.model.outbox.OutBox;
import com.social_media.postservice.domain.model.outbox.OutboxStatus;
import com.social_media.postservice.domain.model.post.event.PostCreatedEvent;
import com.social_media.postservice.domain.model.post.valueobject.AuthorSnapshot;
import com.social_media.postservice.domain.repository.OutBoxRepository;
import com.social_media.postservice.infrastructure.client.identity.service.IdentityServiceHelper; // Tiêm Helper mới
import com.social_media.postservice.application.service.MediaService;
import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.model.post.valueobject.PostMedia;
import com.social_media.postservice.domain.model.post.valueobject.MediaType;
import com.social_media.postservice.domain.repository.PostRepository;
import com.social_media.postservice.infrastructure.client.profile.ProfileClient;
import com.social_media.postservice.infrastructure.client.profile.service.ProfileServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePostUseCase {

    private final MediaService mediaService;
    private final PostRepository postRepository;
    private final IdentityServiceHelper identityServiceHelper;
    private final PostEventPublisher postEventPublisher;
    private final ProfileClient profileClient;
    private final OutBoxRepository outBoxRepository;
    private final ObjectMapper objectMapper;
    private final ProfileServiceHelper profileServiceHelper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public PostResponse execute(CreatePostCommand command) {

        String statusUser = identityServiceHelper.getSafeUserStatus(command.getUserId());

        String idMaHopLe = "99999999-9999-9999-9999-999999999999";
        //String statusUser = identityServiceHelper.getSafeUserStatus(UUID.fromString(idMaHopLe));
        if ("BANNED".equals(statusUser)) {
            throw new UnauthorizedActionException();
        }

        List<UploadResponse> uploads = mediaService.upload(command.getImages());

        AuthorSnapshot author = profileServiceHelper.getAuthorSnapshot(command.getUserId());

//        Post post = Post.create(command.getUserId(),authorName,authorAvatar, command.getCaption(), command.getLocationName());

        Post post = Post.create(
                command.getUserId(),
                author.getName(),
                author.getAvatarUrl(),
                command.getCaption(),
                command.getLocationName()
        );

        int index = 0;
        for (UploadResponse file : uploads) {
            PostMedia media = PostMedia.create(
                    file.getPublicId(),
                    file.getUrl(),
                    MediaType.IMAGE,
                    index++
            );
            post.addMedia(media);
        }

        try {
            Post savedPost = postRepository.save(post);
            //Send Message
            PostCreatedIntegrationEvent postCreatedIntegrationEvent = new PostCreatedIntegrationEvent(
                    UUID.randomUUID().toString(),
                    savedPost.getId().toString(),
                    SecurityUtils.getCurrentUserId().toString(),
                    savedPost.getAuthor().getName(),
                    savedPost.getAuthor().getAvatarUrl(),
                    savedPost.getCaption(),
                    savedPost.getCreatedAt()
            );
//            postEventPublisher.publishPostCreated(postCreatedIntegrationEvent);

            OutBox outBox = OutBox.builder()
                    .id(UUID.randomUUID())
                    .topic("post-created")
                    .eventType("CREATED POST")
                    .payload(objectMapper.writeValueAsString(postCreatedIntegrationEvent))
                    .status(OutboxStatus.NEW)
                    .createdAt(LocalDateTime.now())
                    .build();

            outBoxRepository.save(outBox);

            applicationEventPublisher.publishEvent(
                    new PostCreatedEvent(savedPost.getId(),savedPost.getCaption())
            );



            return PostResponse.from(savedPost);
        } catch (Exception e) {
            log.error("Save DB failed, rolling back Cloudinary...");
            for (UploadResponse file : uploads) {
                try {
                    mediaService.deleteFile(file.getPublicId());
                } catch (Exception ex) {
                    log.error("Failed to rollback image on Cloudinary: " + file.getPublicId(), ex);
                }
            }
            throw new CloudinaryUploadException(e);
        }
    }
}