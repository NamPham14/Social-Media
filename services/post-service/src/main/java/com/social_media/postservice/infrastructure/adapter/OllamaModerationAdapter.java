package com.social_media.postservice.infrastructure.adapter;


import com.social_media.postservice.domain.model.post.service.ModerationPort;
import com.social_media.postservice.domain.model.post.valueobject.ModerationResult;
import com.social_media.postservice.domain.model.post.valueobject.ModerationStatus;
//import com.social_media.postservice.infrastructure.client.moderation.service.GeminiModerationHelper;
import com.social_media.postservice.infrastructure.client.moderation.dto.ModerationScores;
import com.social_media.postservice.infrastructure.client.moderation.service.OllamaModerationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OllamaModerationAdapter implements ModerationPort {

//    private final GeminiModerationHelper geminiModerationHelper;
    private final OllamaModerationHelper ollamaModerationHelper;

    private static final String VIOLATION_REASON =
            "Nội dung chứa ngôn từ vi phạm tiêu chuẩn cộng đồng (ngôn từ kích động, xúc phạm hoặc bạo lực).";

    @Override
    public ModerationResult check(String content) {
        //ModerationScores scores = geminiModerationHelper.checkContent(content);
        ModerationScores scores = ollamaModerationHelper.checkContent(content);

        return scores.violated()
                ? new ModerationResult(ModerationStatus.REMOVED, VIOLATION_REASON)
                : new ModerationResult(ModerationStatus.NONE, null);
    }
}