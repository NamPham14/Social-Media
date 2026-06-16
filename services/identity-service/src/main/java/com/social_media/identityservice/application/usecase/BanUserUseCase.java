package com.social_media.identityservice.application.usecase;


import com.social_media.identityservice.application.exception.user.UserNotFoundException;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BanUserUseCase {
    private final UserRepository userRepository;

    @Transactional
    public void banUser(UserId id){

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        user.banAccount();

        userRepository.save(user);
    }
}
