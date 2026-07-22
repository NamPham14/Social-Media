package com.social_media.interactionservice.domain.model;

import java.util.List;

public record InteractionPage(List<Interaction> content, int page, int size,
                              long totalElements, int totalPages) { }
