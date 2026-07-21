package com.social_media.postservice.application.usecase.report;

import com.social_media.postservice.application.command.ReportPostCommand;
import com.social_media.postservice.application.exception.ResourceNotFoundException;
import com.social_media.postservice.domain.model.post.aggregate.Post;
//import com.social_media.postservice.domain.model.report.aggregate.Report;
import com.social_media.postservice.domain.model.report.aggregate.Report;
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
                .orElseThrow(() -> new ResourceNotFoundException());

        if (post.isDeleted()) {
            throw new ResourceNotFoundException();
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

