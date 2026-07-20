package com.social_media.interactionservice.domain.exception;

public class DependencyRejectedException extends DependencyUnavailableException {
    public DependencyRejectedException(String dependency, int status) {
        super(dependency, "rejected the availability request with HTTP " + status);
    }
}
