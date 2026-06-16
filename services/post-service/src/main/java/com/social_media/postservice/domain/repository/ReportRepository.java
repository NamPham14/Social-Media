package com.social_media.postservice.domain.repository;

//import com.social_media.postservice.domain.model.report.aggregate.Report;
//import com.social_media.postservice.domain.model.report.valueobject.ReportStatus;
import com.social_media.postservice.domain.model.report.aggregate.Report;
import com.social_media.postservice.domain.model.report.valueobject.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository {

    Optional<Report> findById(UUID id);

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    Report save(Report report);
}

