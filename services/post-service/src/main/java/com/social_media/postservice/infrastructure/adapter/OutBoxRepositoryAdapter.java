package com.social_media.postservice.infrastructure.adapter;

import com.social_media.postservice.domain.model.outbox.OutBox;
import com.social_media.postservice.domain.model.outbox.OutboxStatus;
import com.social_media.postservice.domain.repository.OutBoxRepository;
import com.social_media.postservice.infrastructure.repository.OutBoxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class OutBoxRepositoryAdapter implements OutBoxRepository {

    private final OutBoxJpaRepository outBoxJpaRepository;

    @Override
    public OutBox findById(UUID id) {
        return outBoxJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<OutBox> findAll() {
        return outBoxJpaRepository.findAll();
    }

    @Override
    public OutBox save(OutBox outBox) {
        return outBoxJpaRepository.save(outBox);
    }

    @Override
    public OutBox update(OutBox outBox) {
        return outBoxJpaRepository.save(outBox);
    }

    @Override
    public void delete(UUID uuid) {
         outBoxJpaRepository.deleteById(uuid);
    }

    @Override
    public List<OutBox> findByStatus(OutboxStatus status) {
        return outBoxJpaRepository.findByStatusOrderByCreatedAtAsc(status);
    }
}
