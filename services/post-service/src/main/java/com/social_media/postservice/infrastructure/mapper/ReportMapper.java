package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.Report;
import com.social_media.postservice.infrastructure.entity.ReportEntity;

public interface ReportMapper {

    ReportEntity toEntity(Report domain);

    Report toDomain(ReportEntity entity);
}
