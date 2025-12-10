package com.sprint.sb06deokhugamteam01.dto;

import java.time.LocalDateTime;
import java.util.Map;


import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;
import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder
public record ErrorDto (

    String message,
    int status,
    String code,
    Map<String, Object> details,

    LocalDateTime timestamp,
    String exceptionType

){
    public static ResponseEntity<ErrorDto> toResponseEntity(Exception exception) {
        ErrorCode internalError = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(internalError.getStatus())
                .body(ErrorDto.builder()
                        .message(internalError.getMessage())
                        .status(internalError.getStatus())
                        .code(internalError.getCode())
                        .timestamp(LocalDateTime.now())
                        .exceptionType(exception.getClass().getSimpleName())
                        .build());
    }

    public static ResponseEntity<ErrorDto> toResponseEntity(RootException exception){
        return ResponseEntity
                .status(exception.getErrorCode().getStatus())
                .body(ErrorDto.builder()
                        .message(exception.getErrorCode().getMessage())
                        .status(exception.getErrorCode().getStatus())
                        .code(exception.getErrorCode().getCode())
                        .details(exception.getDetails())
                        .timestamp(exception.getTimestamp())
                        .exceptionType(exception.getClass().getSimpleName())
                        .build());
    }
}
