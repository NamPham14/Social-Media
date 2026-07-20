package com.social_media.postservice.infrastructure.adapter;

//import com.social_media.postservice.domain.model.report.aggregate.Report;
import com.social_media.postservice.domain.model.report.aggregate.Report;
import com.social_media.postservice.domain.model.report.valueobject.ReportStatus;
import com.social_media.postservice.domain.repository.ReportRepository;
//import com.social_media.postservice.domain.model.report.valueobject.ReportStatus;
import com.social_media.postservice.infrastructure.entity.ReportEntity;
import com.social_media.postservice.infrastructure.mapper.ReportMapper;
import com.social_media.postservice.infrastructure.repository.ReportJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportRepositoryAdapter implements ReportRepository {

    private final ReportJpaRepository reportJpaRepository;
    private final ReportMapper reportMapper;

    @Override
    public Optional<Report> findById(UUID id) {
        return reportJpaRepository.findById(id)
                .map(reportMapper::toDomain);
    }

    @Override
    public Page<Report> findByStatus(ReportStatus status, Pageable pageable) {
        return reportJpaRepository.findByStatus(status, pageable)
                .map(reportMapper::toDomain);
    }

    @Override
    public Report save(Report report) {
        ReportEntity entity = reportMapper.toEntity(report);
        ReportEntity saved = reportJpaRepository.save(entity);
        return reportMapper.toDomain(saved);
    }

    @Override
    public void deleteReportByPostId(UUID postId) {
        reportJpaRepository.deleteByPostId(postId);
    }
}

