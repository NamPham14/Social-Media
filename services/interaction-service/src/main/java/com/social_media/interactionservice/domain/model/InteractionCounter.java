package com.social_media.interactionservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "interaction_counters")
@Getter
@NoArgsConstructor
public class InteractionCounter {

    @EmbeddedId
    private InteractionCounterId id;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "clap_count", nullable = false)
    private int clapCount;

    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
