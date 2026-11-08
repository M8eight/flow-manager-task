package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;

public class NotFoundException extends BaseException {
    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
