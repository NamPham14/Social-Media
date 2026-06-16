package com.social_media.identityservice.application.command;




import lombok.Builder;

@Builder
public record RegisterCommand(String username, String password, String email) {

}
