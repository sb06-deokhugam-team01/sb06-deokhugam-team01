package com.sprint.sb06deokhugamteam01.exception.comment;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class CommentAccessDeniedException extends RootException {
    public CommentAccessDeniedException() {
        super(ErrorCode.COMMENT_ACCESS_DENIED);
    }
    public CommentAccessDeniedException(Map<String, Object> details) {
        super(ErrorCode.COMMENT_ACCESS_DENIED, details);
    }
}
