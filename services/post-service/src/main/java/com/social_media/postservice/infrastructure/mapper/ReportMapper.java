package com.social_media.postservice.infrastructure.mapper;

import com.social_media.postservice.domain.aggreate.Report;
import com.social_media.postservice.infrastructure.entity.ReportEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportEntity toEntity(Report domain);

    Report toDomain(ReportEntity entity);
}
