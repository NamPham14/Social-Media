package com.social_media.postservice.domain.repository;

import com.social_media.postservice.domain.aggregate.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository {

    Optional<Report> findById(UUID id);

    Page<Report> findByStatus(com.social_media.postservice.domain.valueobject.ReportStatus status, Pageable pageable);

    Report save(Report report);
}
