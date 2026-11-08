package com.task.flow_manager.application.exception;

import com.task.flow_manager.application.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, ErrorCode errorCode, Throwable e) {
        super(message);
        this.errorCode = errorCode;
    }

}
