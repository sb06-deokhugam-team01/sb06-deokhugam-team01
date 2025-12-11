package com.sprint.sb06deokhugamteam01.exception.user;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;
import java.util.Map;

public class UserNotFoundException extends RootException {

    ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

    public UserNotFoundException(Map<String, Object> details) {
        super(ErrorCode.USER_NOT_FOUND, details);
    }

}
