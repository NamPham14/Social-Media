package com.social_media.identityservice.application.usecase;

import com.social_media.identityservice.application.command.ChangePasswordCommand;
import com.social_media.identityservice.application.exception.user.UserNotFoundException;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void execute(UserId id, ChangePasswordCommand command){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        if(!passwordEncoder.matches(command.oldPassword(),user.getPassword())){
            throw  new  RuntimeException("Mật khẩu cũ không chính xác!");
        }

        String hashedNewPassword = passwordEncoder.encode(command.newPassword());
        user.changePassword(hashedNewPassword);
        userRepository.save(user);
    }

}
