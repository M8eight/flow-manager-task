package com.task.flow_manager.application.dto.web;

import com.task.flow_manager.application.enums.ErrorCode;
import com.task.flow_manager.application.enums.FileStatus;

import java.time.Instant;

public record FileMetadataResponse(
        FileStatus status,
        ErrorCode errorCode,
        String errorMessage,
        Instant processedAt
) {
}
