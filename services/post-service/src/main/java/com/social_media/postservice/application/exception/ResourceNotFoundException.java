package com.social_media.postservice.application.exception;

import com.social_media.common.exception.EntityNotFoundException;

public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException() {
        super(PostError.RESOURCE_NOT_FOUND.getMessage());
    }
}
