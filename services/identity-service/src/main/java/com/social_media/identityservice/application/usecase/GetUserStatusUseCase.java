package com.social_media.identityservice.application.usecase;


import com.social_media.identityservice.application.exception.user.UserNotFoundException;
import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import com.social_media.identityservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserStatusUseCase {
    private final UserRepository userRepository;

    public String getStatus(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());
        return user.getStatus().name();
    }
}
