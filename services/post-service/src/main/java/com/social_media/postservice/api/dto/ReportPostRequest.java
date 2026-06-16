package com.social_media.postservice.api.dto;

import com.social_media.postservice.domain.model.report.valueobject.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportPostRequest {

    @NotNull
    private UUID reporterId;

    @NotNull
    private ReportReason reason;

    private String description;
}

