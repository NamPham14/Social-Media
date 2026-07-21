package com.social_media.commentservice.infrastructure.client.post;

import java.util.UUID;

public record PostSnapshot(UUID id, UUID userId, String status) { }
