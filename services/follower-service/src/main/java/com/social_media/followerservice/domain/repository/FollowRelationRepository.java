package com.social_media.followerservice.domain.repository;

import com.social_media.followerservice.domain.model.follow.aggregate.FollowRelation;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface FollowRelationRepository {
    FollowRelation save(FollowRelation followRelation);
    Optional<FollowRelation> findById(Long id);
    boolean existsByFollowerIdAndFollowingId(UserId followerId, UserId followingId);
    Page<FollowRelation> findByFollowerId(UserId followerId, Pageable pageable);
    Page<FollowRelation> findByFollowingId(UserId followingId, Pageable pageable);
    void deleteByFollowerIdAndFollowingId(UserId followerId, UserId followingId);
}
