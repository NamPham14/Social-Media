package com.social_media.interactionservice.infrastructure.messaging.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class KafkaConsumerErrorConfigTest {

    @Test
    void createsDefaultErrorHandlerForRetryAndDltRecovery() {
        KafkaTemplate<Object, Object> kafkaTemplate = mock(KafkaTemplate.class);

        var handler = new KafkaConsumerErrorConfig().interactionKafkaErrorHandler(
                kafkaTemplate, 5, 1000, "-interaction-dlt", 10);

        assertThat(handler).isInstanceOf(DefaultErrorHandler.class);
    }
}
