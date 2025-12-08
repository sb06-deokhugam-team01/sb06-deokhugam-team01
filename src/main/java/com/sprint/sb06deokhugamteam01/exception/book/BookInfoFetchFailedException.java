package com.sprint.sb06deokhugamteam01.exception.book;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class BookInfoFetchFailedException extends RootException {
    public BookInfoFetchFailedException(Map<String, Object> details) {
        super(ErrorCode.BOOK_INFO_FETCH_FAILED, details);
    }
}
