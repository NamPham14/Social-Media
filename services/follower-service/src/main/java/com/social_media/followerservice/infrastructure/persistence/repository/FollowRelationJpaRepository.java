package com.social_media.followerservice.infrastructure.persistence.repository;

import com.social_media.followerservice.infrastructure.persistence.entity.FollowRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRelationJpaRepository extends JpaRepository<FollowRelationEntity, Long> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Page<FollowRelationEntity> findByFollowerId(Long followerId, Pageable pageable);
    Page<FollowRelationEntity> findByFollowingId(Long followingId, Pageable pageable);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
