package com.social_media.postservice.application.usecase;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.ReportPostCommand;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.aggreate.Post;
import com.social_media.postservice.domain.aggreate.Report;
import com.social_media.postservice.domain.repository.PostRepository;
import com.social_media.postservice.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportPostUseCase {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public void execute(ReportPostCommand command) {
        Post post = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (post.isDeleted()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Report report = Report.create(
                command.getPostId(),
                command.getReporterId(),
                command.getReason(),
                command.getDescription()
        );

        reportRepository.save(report);
    }
}
