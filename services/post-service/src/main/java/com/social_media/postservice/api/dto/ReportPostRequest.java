package com.social_media.postservice.api.dto;

import com.social_media.postservice.domain.model.report.valueobject.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportPostRequest {

    @NotNull
    private ReportReason reason;

    private String description;
}

