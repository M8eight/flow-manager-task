package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;

public class BadRequestException extends BaseException {
    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
