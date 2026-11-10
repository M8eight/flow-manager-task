package com.task.flow_manager.application.dto.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FileConversionRequestedEvent(
        UUID eventId,
        String bucket,
        String objectKey
) {
}