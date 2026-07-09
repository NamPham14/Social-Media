package com.social_media.identityservice.application.usecase;


import com.social_media.identityservice.application.exception.user.UserNotFoundException;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnbanUserUseCase {
    private final UserRepository userRepository;

    @Transactional
    public void unbanUser(UserId userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);


        user.unBanAccount();


        userRepository.save(user);
    }
}
