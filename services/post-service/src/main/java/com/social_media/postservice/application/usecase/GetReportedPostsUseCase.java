package com.social_media.postservice.application.usecase;

import com.social_media.postservice.domain.aggreate.Report;
import com.social_media.postservice.domain.repository.ReportRepository;
import com.social_media.postservice.domain.valueobject.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetReportedPostsUseCase {

    private final ReportRepository reportRepository;

    public Page<Report> execute(Pageable pageable) {
        return reportRepository.findByStatus(ReportStatus.PENDING, pageable);
    }
}
