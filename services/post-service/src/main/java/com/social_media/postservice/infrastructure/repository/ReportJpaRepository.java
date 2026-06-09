package com.social_media.postservice.infrastructure.repository;

import com.social_media.postservice.domain.valueobject.ReportStatus;
import com.social_media.postservice.infrastructure.entity.ReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportEntity, UUID> {

    Page<ReportEntity> findByStatus(ReportStatus status, Pageable pageable);
}
