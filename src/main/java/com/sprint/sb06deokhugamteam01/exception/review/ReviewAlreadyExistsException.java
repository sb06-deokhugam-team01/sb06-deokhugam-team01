package com.sprint.sb06deokhugamteam01.exception.review;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class ReviewAlreadyExistsException extends RootException {

    ErrorCode errorCode = ErrorCode.INVALID_REVIEW_CURSOR;

    public ReviewAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.INVALID_REVIEW_CURSOR, details);
    }
}
