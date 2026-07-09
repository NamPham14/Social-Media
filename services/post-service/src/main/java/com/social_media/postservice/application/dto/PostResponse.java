package com.social_media.postservice.application.dto;


import com.social_media.postservice.domain.model.post.aggregate.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Getter
@Setter
public class PostResponse {

    private UUID id;
    private UUID userId;

    private String caption;
    private String locationName;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<MediaResponse> medias;

    public static PostResponse from(Post post) {
        PostResponse res = new PostResponse();

        res.id = post.getId();
        res.userId = post.getUserId();
        res.caption = post.getCaption();
        res.locationName = post.getLocationName();

        res.status = post.getStatus().name();

        res.createdAt = post.getCreatedAt();
        res.updatedAt = post.getUpdatedAt();

        res.medias = post.getMedias()
                .stream()
                .map(MediaResponse::from)
                .collect(Collectors.toList());

        return res;
    }
}
