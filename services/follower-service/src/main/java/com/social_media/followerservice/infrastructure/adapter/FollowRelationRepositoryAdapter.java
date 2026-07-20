package com.social_media.followerservice.infrastructure.adapter;

import com.social_media.followerservice.domain.model.follow.aggregate.FollowRelation;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import com.social_media.followerservice.infrastructure.mapper.FollowPersistenceMapper;
import com.social_media.followerservice.infrastructure.persistence.entity.FollowRelationEntity;
import com.social_media.followerservice.infrastructure.persistence.repository.FollowRelationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FollowRelationRepositoryAdapter implements FollowRelationRepository {
    private final FollowRelationJpaRepository jpaRepository;
    private final FollowPersistenceMapper mapper;

    @Override public FollowRelation save(FollowRelation domain) {
        FollowRelationEntity saved = jpaRepository.save(mapper.toEntity(domain));
        return mapper.toDomain(saved);
    }
    @Override public Optional<FollowRelation> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);

    }
    @Override public boolean existsByFollowerIdAndFollowingId(UserId followerId, UserId followingId) {
        return jpaRepository.existsByFollowerIdAndFollowingId(followerId.value(), followingId.value());
    }

    @Override public Page<FollowRelation> findByFollowerId(UserId followerId, Pageable p) {
        return jpaRepository.findByFollowerId(followerId.value(), p).map(mapper::toDomain);
    }

    @Override public Page<FollowRelation> findByFollowingId(UserId followingId, Pageable p) {
        return jpaRepository.findByFollowingId(followingId.value(), p).map(mapper::toDomain);
    }
    @Override public void deleteByFollowerIdAndFollowingId(UserId followerId, UserId followingId) {
        jpaRepository.deleteByFollowerIdAndFollowingId(followerId.value(), followingId.value());
    }
}
