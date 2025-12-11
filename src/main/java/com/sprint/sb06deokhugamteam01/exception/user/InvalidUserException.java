package com.sprint.sb06deokhugamteam01.exception.user;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;
import java.util.Map;

public class InvalidUserException extends RootException {

    ErrorCode errorCode = ErrorCode.INVALID_USER;

    public InvalidUserException(Map<String, Object> details) {
        super(ErrorCode.INVALID_USER, details);
    }
}
