package com.sprint.sb06deokhugamteam01.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class RootException extends RuntimeException {

    private LocalDateTime timestamp;
    private ErrorCode errorCode;
    private final Map<String, Object> details;

    public RootException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.details = details;
    }

}
