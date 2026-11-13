package com.task.flow_manager.application.dto.web;

import com.task.flow_manager.application.enums.SubscriptionStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SubscriptionResponse(
        String login,
        SubscriptionStatus status,
        Instant expiresAt
) {
}
