package com.sprint.sb06deokhugamteam01.exception.book;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class S3DeleteFailedException extends RootException {
    public S3DeleteFailedException(Map<String, Object> details) {
        super(ErrorCode.S3_DELETE_FAILED, details);
    }
}
