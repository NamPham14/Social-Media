package com.social_media.identityservice.application.command;

import lombok.Builder;

@Builder
public record ChangePasswordCommand (String oldPassword, String newPassword){
}
