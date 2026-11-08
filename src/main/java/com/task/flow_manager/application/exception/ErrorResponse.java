package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String message,
        ErrorCode code,
        String path
) {
}
