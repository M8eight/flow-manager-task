package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;

public class ConflictException extends BaseException {
    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
