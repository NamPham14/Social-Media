package com.social_media.identityservice.application.usecase;

import com.social_media.identityservice.domain.model.user.aggregate.User;
import com.social_media.identityservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAllUsersUseCase {
    private final UserRepository userRepository;

    public Page<User> execute(int page, int size, String keyword) {

        Pageable pageable = PageRequest.of(page - 1, size);
        return userRepository.searchUsers(keyword,pageable);
    }
}
