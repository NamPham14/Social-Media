package com.social_media.commentservice.domain.exception;

public class DependencyUnavailableException extends RuntimeException {
    public DependencyUnavailableException(String dependency) {
        this(dependency, "is unavailable; target could not be verified");
    }

    protected DependencyUnavailableException(String dependency, String detail) {
        super(dependency + " " + detail);
    }
}
