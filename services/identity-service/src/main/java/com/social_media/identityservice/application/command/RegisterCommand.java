package com.social_media.identityservice.application.command;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RegisterCommand {
    private String username;
    private String password;
    private String email;
}
