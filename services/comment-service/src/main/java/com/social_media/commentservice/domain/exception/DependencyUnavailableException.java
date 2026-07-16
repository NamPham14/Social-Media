package com.social_media.commentservice.domain.exception;

public class DependencyUnavailableException extends RuntimeException {
    public DependencyUnavailableException(String dependency) {
        super(dependency + " is unavailable; target could not be verified");
    }
}
