package com.social_media.postservice.application.dto;
//import com.social_media.postservice.domain.model.post.valueobject.PostMedia;
import com.social_media.postservice.domain.model.post.valueobject.PostMedia;
import lombok.Getter;

import java.util.UUID;


@Getter
public class MediaResponse {

    private UUID id;
    private String url;
    private String type;

    public static MediaResponse from(PostMedia media) {
        MediaResponse res = new MediaResponse();
        res.id = media.getId();
        res.url = media.getMediaUrl();
        res.type = media.getMediaType().name();
        return res;
    }
}

