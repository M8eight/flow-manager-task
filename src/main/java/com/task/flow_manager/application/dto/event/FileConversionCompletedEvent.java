package com.task.flow_manager.application.dto.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FileConversionCompletedEvent(
        UUID eventId,
        UUID correlationId,
        String bucket,
        String errorCode,
        String errorMessage
) {
}
