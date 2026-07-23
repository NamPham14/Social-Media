package com.social_media.postservice.domain.model.post.valueobject;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AuthorSnapshot {
    private String name;
    private String avatarUrl;
}
