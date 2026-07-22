package com.social_media.chatservice.infrastructure.messaging.producer;

import com.social_media.chatservice.application.dto.events.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${messaging.topics.message-sent}")
    private String messageSentTopic;

    public void sendMessageSentEvent(MessageSentEvent event) {
        kafkaTemplate.send(messageSentTopic, event.conversationId().toString(), event);
    }
}
