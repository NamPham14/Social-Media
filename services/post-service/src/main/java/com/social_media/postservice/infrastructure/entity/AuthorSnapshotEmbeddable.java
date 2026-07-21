package com.social_media.postservice.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorSnapshotEmbeddable {

    @Column(name = "author_name", length = 100)
    private String name;

    @Column(name = "author_avatar_url", length = 500)
    private String avatarUrl;
}