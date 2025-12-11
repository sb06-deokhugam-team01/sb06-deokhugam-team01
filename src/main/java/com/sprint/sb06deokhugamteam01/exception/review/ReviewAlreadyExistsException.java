package com.sprint.sb06deokhugamteam01.exception.review;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class ReviewAlreadyExistsException extends RootException {

    public ReviewAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.REVIEW_ALREADY_EXISTS, details);
    }
}
