package com.social_media.followerservice.application.usecase;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class GetFollowingUseCaseImpl implements GetFollowingUseCase {

    @Override
    public List<Long> getFollowing(Long userId) {

        return Collections.emptyList();
    }
}
