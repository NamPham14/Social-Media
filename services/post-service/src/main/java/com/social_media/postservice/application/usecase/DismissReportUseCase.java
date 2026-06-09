package com.social_media.postservice.application.usecase;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.ReviewReportCommand;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.aggregate.Report;
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
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        report.dismiss();
        reportRepository.save(report);
    }
}
