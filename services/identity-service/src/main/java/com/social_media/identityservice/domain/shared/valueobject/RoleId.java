package com.social_media.identityservice.domain.shared.valueobject;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.util.Objects;

public record RoleId(String value) implements Serializable {
    public RoleId {
        Objects.requireNonNull(value, "Role ID is required");
        if (value.isBlank()) throw new IllegalArgumentException("Role ID cannot be blank");
    }

    public static RoleId from(String value) {
        return new RoleId(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
