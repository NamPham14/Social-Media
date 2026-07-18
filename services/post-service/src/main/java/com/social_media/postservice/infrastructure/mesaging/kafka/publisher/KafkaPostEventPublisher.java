package com.social_media.postservice.infrastructure.mesaging.kafka.publisher;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.postservice.application.dto.events.PostCreatedIntegrationEvent;
import com.social_media.postservice.application.dto.events.PostDeleteIntegrationEvent;
import com.social_media.postservice.application.ports.output.PostEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostEventPublisher implements PostEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String TOPIC = "post-created-topic";
    private final String TOPIC2 = "post-deleted-topic";



    @Override
    public void publishPostCreated(PostCreatedIntegrationEvent event) {
        log.info("Post Created Event Send: {}", event.getId());

        kafkaTemplate.send(TOPIC, event)
                .whenComplete((res, ex) -> {

                    if (ex == null) {
                        log.info("Post Created Event Send Success: {}", event.getId());
                        log.info("Event: {}", event);
                    }else  {
                        log.error("Post Created Event Send Failed: {}", ex.getMessage());
                    }

                });
    }

    @Override
    public void publishPostDelete(PostDeleteIntegrationEvent event) {
        log.info("Post Delete Event Send: {}", event.getId());

        kafkaTemplate.send(TOPIC2, event)
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("Post Delete Event Send Success: {}", event.getId());
                        log.info("Event: {}", event);
                    }else  {
                        log.error("Post Delete Event Send Failed: {}", ex.getMessage());
                    }
                });

    }



}
