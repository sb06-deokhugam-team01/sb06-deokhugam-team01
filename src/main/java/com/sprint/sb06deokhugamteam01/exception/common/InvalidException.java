package com.sprint.sb06deokhugamteam01.exception.common;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;
import java.util.Map;

public class InvalidException extends RootException {

    ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
    public InvalidException(Map<String, Object> detail) {
        super(ErrorCode.INVALID_REQUEST, detail);
    }
}
