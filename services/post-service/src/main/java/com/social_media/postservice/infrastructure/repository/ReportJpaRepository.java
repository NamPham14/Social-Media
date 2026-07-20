package com.social_media.postservice.infrastructure.repository;

import com.social_media.postservice.domain.model.report.valueobject.ReportStatus;
import com.social_media.postservice.infrastructure.entity.ReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportEntity, UUID> {

    Page<ReportEntity> findByStatus(ReportStatus status, Pageable pageable);

    @Modifying
    @Query("DELETE from ReportEntity r where r.postId = :postId")
    void deleteByPostId(@Param("postId") UUID postId);
}

