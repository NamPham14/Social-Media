package com.social_media.chatservice.application.exception;

import com.social_media.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatError implements ErrorCode {
    CONVERSATION_NOT_FOUND(3001, "Conversation not found", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(3002, "Message not found", HttpStatus.NOT_FOUND),
    NOT_A_PARTICIPANT(3003, "You are not a participant of this conversation", HttpStatus.FORBIDDEN),
    CANNOT_CREATE_WITH_SELF(3004, "Cannot create conversation with yourself", HttpStatus.BAD_REQUEST),
    NOT_SENDER(3005, "Only the sender can delete this message", HttpStatus.FORBIDDEN),
    NOT_FOLLOWING(3006, "You must follow this user to start a conversation", HttpStatus.FORBIDDEN),
    INVALID_INPUT(3007, "Invalid input data", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
