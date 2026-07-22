package com.social_media.followerservice.infrastructure.persistence.repository;

import com.social_media.followerservice.infrastructure.persistence.entity.FollowRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRelationJpaRepository extends JpaRepository<FollowRelationEntity, UUID> {

    Page<FollowRelationEntity> findByFollowerId(UUID followerId, Pageable pageable);

    // hiếu thêm
    List<FollowRelationEntity> findByFollowerId(UUID followerId);

    Page<FollowRelationEntity> findByFollowingId(UUID followingId, Pageable pageable);

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
}
