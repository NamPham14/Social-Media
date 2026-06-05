package com.social_media.followerservice.application.usecase;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class GetFollowersUseCaseImpl implements GetFollowersUseCase {

    @Override
    public List<Long> getFollowers(Long userId) {

        return Collections.emptyList();
    }
}
