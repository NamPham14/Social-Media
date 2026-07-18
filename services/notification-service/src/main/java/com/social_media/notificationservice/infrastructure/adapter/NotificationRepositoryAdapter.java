package com.social_media.notificationservice.infrastructure.adapter;

import com.social_media.notificationservice.domain.model.aggregate.Notification;
import com.social_media.notificationservice.domain.repository.NotificationRepository;
import com.social_media.notificationservice.infrastructure.entity.NotificationEntity;
import com.social_media.notificationservice.infrastructure.mapper.NotificationPersistenceMapper;
import com.social_media.notificationservice.infrastructure.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {
    private static final int MAX_PAGE_SIZE = 100;

    private final NotificationJpaRepository jpaRepository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = mapper.toEntity(notification);
        NotificationEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Notification> findByIdAndRecipientId(Long id, String recipientId) {
        return jpaRepository.findByIdAndRecipientId(id, recipientId).map(mapper::toDomain);
    }

    @Override
    public Optional<Notification> findBySourceEventId(String sourceEventId) {
        return jpaRepository.findBySourceEventId(sourceEventId).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySourceEventId(String sourceEventId) {
        return jpaRepository.existsBySourceEventId(sourceEventId);
    }

    @Override
    public List<Notification> findByRecipientId(String recipientId, int limit) {
        return jpaRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageRequest(limit))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Notification> findUnreadByRecipientId(String recipientId, int limit) {
        return jpaRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId, pageRequest(limit))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByRecipientId(String recipientId) {
        return jpaRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }

    @Override
    public void delete(Notification notification) {
        jpaRepository.delete(mapper.toEntity(notification));
    }

    private Pageable pageRequest(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be greater than 0");
        }

        return PageRequest.of(0, Math.min(limit, MAX_PAGE_SIZE));
    }
}
