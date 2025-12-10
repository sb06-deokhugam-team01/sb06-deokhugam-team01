package com.sprint.sb06deokhugamteam01.exception.review;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class ReviewNotFoundException extends RootException {

    ErrorCode errorCode = ErrorCode.REVIEW_NOT_FOUND;

    public ReviewNotFoundException(Map<String, Object> details) {
        super(ErrorCode.REVIEW_NOT_FOUND, details);
    }

}
