package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;

public class FileConversionException extends BaseException {
    public FileConversionException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
