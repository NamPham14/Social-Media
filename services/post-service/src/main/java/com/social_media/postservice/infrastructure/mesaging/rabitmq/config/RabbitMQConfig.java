package com.social_media.postservice.infrastructure.mesaging.rabitmq.config;



import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;


@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public TopicExchange postExchange() {
//        return new TopicExchange("post.exchange");
        return ExchangeBuilder.topicExchange("post-exchange").durable(true).build();
    }

    @Bean
    public Queue postQueue() {
        return new Queue("post.queue");
    }

    @Bean
    public Binding postBinding(Queue postQueue, TopicExchange postExchange) {
        return BindingBuilder
                .bind(postQueue)
                .to(postExchange)
                .with("post.*");
    }





}
