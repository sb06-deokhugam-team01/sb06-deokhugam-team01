package com.sprint.sb06deokhugamteam01.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sprint.sb06deokhugamteam01.dto.ErrorDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RootException.class)
    public ResponseEntity<ErrorDto> handleRootException(RootException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Handled custom exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorDto.builder()
                        .message(errorCode.getMessage())
                        .status(errorCode.getStatus())
                        .code(errorCode.getCode())
                        .details(ex.getDetails())
                        .timestamp(ex.getTimestamp())
                        .exceptionType(ex.getClass().getSimpleName())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        Map<String, Object> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing));

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorDto.builder()
                        .message(errorCode.getMessage())
                        .status(errorCode.getStatus())
                        .code(errorCode.getCode())
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .exceptionType(ex.getClass().getSimpleName())
                        .build());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorDto> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        Map<String, Object> details = Map.of("missingHeader", ex.getHeaderName());

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorDto.builder()
                        .message(errorCode.getMessage())
                        .status(errorCode.getStatus())
                        .code(errorCode.getCode())
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .exceptionType(ex.getClass().getSimpleName())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneralException(Exception ex) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Unhandled exception", ex);

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorDto.builder()
                        .message(errorCode.getMessage())
                        .status(errorCode.getStatus())
                        .code(errorCode.getCode())
                        .details(Map.of())
                        .timestamp(LocalDateTime.now())
                        .exceptionType(ex.getClass().getSimpleName())
                        .build());
    }

}
