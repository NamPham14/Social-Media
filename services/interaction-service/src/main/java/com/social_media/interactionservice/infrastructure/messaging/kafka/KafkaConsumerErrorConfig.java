package com.social_media.interactionservice.infrastructure.messaging.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaConsumerErrorConfig {

    @Bean
    public CommonErrorHandler interactionKafkaErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${messaging.retry.max-attempts:5}") long maxAttempts,
            @Value("${messaging.retry.backoff-ms:1000}") long backoffMs,
            @Value("${messaging.retry.dlt-suffix:-dlt}") String dltSuffix) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, failure) -> new TopicPartition(record.topic() + dltSuffix, record.partition())
        );
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(backoffMs, Math.max(maxAttempts - 1, 0))
        );
        errorHandler.setRetryListeners((record, failure, deliveryAttempt) ->
                log.warn("Retrying Kafka message topic={} partition={} offset={} attempt={}",
                        record.topic(), record.partition(), record.offset(), deliveryAttempt, failure));
        return errorHandler;
    }
}
