package com.sprint.sb06deokhugamteam01.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class RootException extends RuntimeException {

    private final LocalDateTime timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public RootException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.details = details;
    }

    public RootException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
}