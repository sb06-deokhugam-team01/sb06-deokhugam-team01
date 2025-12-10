package com.sprint.sb06deokhugamteam01.exception.common;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;
import java.util.Map;

public class UnauthorizedAccessException extends RootException {

    public UnauthorizedAccessException(Map<String, Object> detail) {
        super(ErrorCode.UNAUTHORIZED_ACCESS, detail);
    }

}
