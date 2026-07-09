package com.social_media.profileservice.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


public record CreateProfileCommand (UUID id, String username,String fullName){

}
