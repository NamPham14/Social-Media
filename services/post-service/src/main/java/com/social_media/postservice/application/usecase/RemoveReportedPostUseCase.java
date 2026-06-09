package com.social_media.postservice.application.usecase;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.application.command.ReviewReportCommand;
import com.social_media.postservice.application.service.MediaService;
import com.social_media.postservice.domain.exception.ErrorCode;
import com.social_media.postservice.domain.aggreate.Post;
import com.social_media.postservice.domain.aggreate.PostMedia;
import com.social_media.postservice.domain.aggreate.Report;
import com.social_media.postservice.domain.repository.PostRepository;
import com.social_media.postservice.domain.valueobject.ReportStatus;
import com.social_media.postservice.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoveReportedPostUseCase {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final MediaService mediaService;

    @Transactional
    public void execute(ReviewReportCommand command) {
        Report report = reportRepository.findById(command.getReportId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new AppException(ErrorCode.REPORT_ALREADY_PROCESSED);
        }

        Post post = postRepository.findById(report.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPTY_RESOURCE));

        post.removeByAdmin(command.getAdminId());
        report.actOn();

        postRepository.save(post);
        reportRepository.save(report);

        List<PostMedia> postMediaList = new ArrayList<>(post.getMedias());
        for (PostMedia postMedia : postMediaList) {
            try {
                mediaService.deleteFile(postMedia.getPublicId());
            } catch (Exception e) {
                log.error("Remove reported post thành công nhưng chưa xóa được ảnh trên Cloudinary cho publicId: "
                        + postMedia.getPublicId(), e);
            }
        }
    }
}
