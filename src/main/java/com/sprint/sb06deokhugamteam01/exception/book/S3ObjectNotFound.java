package com.sprint.sb06deokhugamteam01.exception.book;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class S3ObjectNotFound extends RootException {
    public S3ObjectNotFound(Map<String, Object> details) {
        super(ErrorCode.S3_OBJECT_NOT_FOUND, details);
    }
}
