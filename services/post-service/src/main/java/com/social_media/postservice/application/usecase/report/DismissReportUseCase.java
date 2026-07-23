package com.social_media.postservice.application.usecase.report;

import com.social_media.postservice.application.command.ReviewReportCommand;
import com.social_media.postservice.application.exception.ResourceNotFoundException;
import com.social_media.postservice.domain.model.report.aggregate.Report;
import com.social_media.postservice.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DismissReportUseCase {

    private final ReportRepository reportRepository;

    @Transactional
    public void execute(ReviewReportCommand command) {
        Report report = reportRepository.findById(command.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException());

        report.dismiss();
        reportRepository.save(report);
    }
}

