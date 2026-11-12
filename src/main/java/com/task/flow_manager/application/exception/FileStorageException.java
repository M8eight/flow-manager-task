package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;

public class FileStorageException extends BaseException {
    public FileStorageException(String message, ErrorCode errorCode, Throwable e) {
        super(message, errorCode, e);
    }
}

