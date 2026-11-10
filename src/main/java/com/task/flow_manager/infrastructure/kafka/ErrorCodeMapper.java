package com.task.flow_manager.infrastructure.kafka;

import com.task.flow_manager.application.enums.ErrorCode;
import com.task.flow_manager.application.exception.FileConversionException;

public class ErrorCodeMapper {

    public static ErrorCode mapErrorCode(String converterErrorCode) {
        if (converterErrorCode == null) return null;
        try {
            return ErrorCode.valueOf(converterErrorCode);
        } catch (IllegalArgumentException e) {
            return ErrorCode.INTERNAL_ERROR;
        }
    }

    public static void mapErrorExceptionByCode(ErrorCode errorCode) {
        if (errorCode == null) return;
        switch (errorCode) {
            case TOO_MANY_FILES -> throw new FileConversionException("Too many file in archive", errorCode);
            case ARCHIVE_TOO_LARGE ->  throw new FileConversionException("Archive too large", errorCode);
            case EMPTY_ARCHIVE ->  throw new FileConversionException("Empty archive", errorCode);
            case CORRUPTED_ARCHIVE ->   throw new FileConversionException("Corrupted archive", errorCode);
            case UNSUPPORTED_FILE_TYPE ->   throw new FileConversionException("Unsupported file type", errorCode);

            case TIMEOUT ->  throw new FileConversionException("Conversion time has expired", errorCode);
            default -> throw new FileConversionException("Failed to convert file", errorCode);
        }
    }

}
