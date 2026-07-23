package com.social_media.followerservice.domain.model.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "outbox")
public class Outbox {

    @Id
    private UUID id;
    private String topic;
    private String eventType;
    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    private LocalDateTime createdAt;
}
