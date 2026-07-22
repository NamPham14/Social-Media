package com.social_media.followerservice.infrastructure.persistence.repository;

import com.social_media.followerservice.infrastructure.persistence.entity.FollowRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRelationJpaRepository extends JpaRepository<FollowRelationEntity, UUID> {

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    Page<FollowRelationEntity> findByFollowerId(UUID followerId, Pageable pageable);

    Page<FollowRelationEntity> findByFollowingId(UUID followingId, Pageable pageable);

    @Query("SELECT fr.followingId FROM FollowRelationEntity fr WHERE fr.followerId = :followerId")
    List<UUID> findFollowingIdsByFollowerId(@Param("followerId") UUID followerId);

    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    long countByFollowerId(UUID followerId);

    long countByFollowingId(UUID followingId);
}
