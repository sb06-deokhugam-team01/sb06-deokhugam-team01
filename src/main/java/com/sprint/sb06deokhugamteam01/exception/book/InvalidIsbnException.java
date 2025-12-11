package com.sprint.sb06deokhugamteam01.exception.book;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class InvalidIsbnException extends RootException {
    public InvalidIsbnException(Map<String, Object> details) {
        super(ErrorCode.INVALID_ISBN, details);
    }
}
