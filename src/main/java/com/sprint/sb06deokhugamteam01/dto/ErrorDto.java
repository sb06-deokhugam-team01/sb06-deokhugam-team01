package com.sprint.sb06deokhugamteam01.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
public record ErrorDto (

    String message,
    int status,
    String code,
    Map<String, Object> details,

    LocalDateTime timestamp,
    String exceptionType

){}
