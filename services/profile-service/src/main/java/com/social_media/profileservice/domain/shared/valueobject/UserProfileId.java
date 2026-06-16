package com.social_media.profileservice.domain.shared.valueobject;



import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record UserProfileId(UUID value) implements Serializable {

    public UserProfileId {
        Objects.requireNonNull(value, "User ID is required");
    }

    public static UserProfileId generate() {
        return new UserProfileId(UUID.randomUUID());
    }

    public static UserProfileId from(String value){
        return new UserProfileId(UUID.fromString(value));
    }

    public static UserProfileId from(UUID value){
        return new UserProfileId(value);
    }

    @JsonValue
    public UUID getValue(){
        return value;
    }

}
