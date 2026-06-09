package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.Report;
import com.social_media.postservice.infrastructure.entity.ReportEntity;
import org.springframework.stereotype.Component;

@Component
public class ReportMapperImpl implements ReportMapper {

    @Override
    public ReportEntity toEntity(Report domain) {
        if (domain == null) return null;

        return ReportEntity.builder()
                .id(domain.getId())
                .postId(domain.getPostId())
                .reporterId(domain.getReporterId())
                .reason(domain.getReason())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    @Override
    public Report toDomain(ReportEntity entity) {
        if (entity == null) return null;

        return Report.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .reporterId(entity.getReporterId())
                .reason(entity.getReason())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
