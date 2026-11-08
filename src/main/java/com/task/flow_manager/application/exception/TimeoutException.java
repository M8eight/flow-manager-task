package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;

public class TimeoutException extends BaseException {
    public TimeoutException(String message) {
        super(message, ErrorCode.TIMEOUT);
    }
}
