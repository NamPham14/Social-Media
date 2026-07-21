package com.social_media.followerservice.infrastructure.adapter;

import com.social_media.followerservice.domain.model.outbox.Outbox;
import com.social_media.followerservice.domain.model.outbox.OutboxStatus;
import com.social_media.followerservice.domain.repository.OutBoxRepository;
import com.social_media.followerservice.infrastructure.persistence.repository.OutBoxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OutBoxRepositoryAdapter implements OutBoxRepository{

    private final OutBoxJpaRepository outBoxJpaRepository;

    @Override
    public Outbox findById(UUID id){
        return outBoxJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Outbox> findAll(){
        return  outBoxJpaRepository.findAll();
    }

    @Override
    public Outbox save(Outbox outbox){
        return outBoxJpaRepository.save(outbox);
    }


    @Override
    public Outbox update(Outbox outbox){
        return outBoxJpaRepository.save(outbox);
    }

    @Override
    public void delete(UUID uuid){
        outBoxJpaRepository.deleteById(uuid);
    }

    @Override
    public List<Outbox> findByStatus(OutboxStatus status){
        return outBoxJpaRepository.findByStatusOrderByCreatedAtAsc(status);
    }



}
