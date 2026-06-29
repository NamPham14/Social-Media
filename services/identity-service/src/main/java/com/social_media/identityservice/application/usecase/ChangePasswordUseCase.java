package com.social_media.identityservice.application.usecase;

import com.social_media.common.exception.BusinessRuleViolationException;

import com.social_media.identityservice.application.command.ChangePasswordCommand;
import com.social_media.identityservice.application.exception.user.UserNotFoundException;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import com.social_media.identityservice.domain.model.user.service.DomainPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final DomainPasswordEncoder domainPasswordEncoder;

    public void execute(UserId id, ChangePasswordCommand command){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        user.changePassword(command.oldPassword(), command.newPassword(), domainPasswordEncoder);

        userRepository.save(user);
    }

}
