package com.social_media.postservice.application.exception;

import com.social_media.common.exception.ServiceUnavailableException;

public class CloudinaryUploadException extends ServiceUnavailableException {
    public CloudinaryUploadException() {
        super(PostError.CLOUDINARY_ERROR.getMessage());
    }
}
