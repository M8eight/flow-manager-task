package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;

public class ServiceUnavailableException extends BaseException {
    public ServiceUnavailableException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
