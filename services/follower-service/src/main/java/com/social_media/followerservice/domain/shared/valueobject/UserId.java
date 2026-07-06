package com.social_media.followerservice.domain.shared.valueobject;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.util.Objects;

public record UserId(Long value) implements Serializable {
    public UserId {
        Objects.requireNonNull(value, "User ID is required");
    }

    public static UserId from(Long value) {
        return new UserId(value);
    }

    @JsonValue
    public Long getValue() {
        return value;
    }
}
