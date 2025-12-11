package com.sprint.sb06deokhugamteam01.exception.comment;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class CommentNotFoundException extends RootException {
    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }
    public CommentNotFoundException(Map<String, Object> details) {
        super(ErrorCode.COMMENT_NOT_FOUND, details);
    }
}
