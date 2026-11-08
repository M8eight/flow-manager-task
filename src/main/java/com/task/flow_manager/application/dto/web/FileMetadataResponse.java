package com.task.flow_manager.application.dto.web;

import com.task.flow_manager.application.enums.ErrorCode;
import com.task.flow_manager.application.enums.FileStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record FileMetadataResponse(
        FileStatus status,
        ErrorCode errorCode,
        String errorMessage,
        Instant processedAt
) {
}
