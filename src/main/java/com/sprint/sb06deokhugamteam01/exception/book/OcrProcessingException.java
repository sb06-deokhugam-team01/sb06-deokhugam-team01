package com.sprint.sb06deokhugamteam01.exception.book;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class OcrProcessingException extends RootException {

    ErrorCode errorCode = ErrorCode.OCR_PROCESSING_FAILED;

    public OcrProcessingException(Map<String, Object> details) {
        super(ErrorCode.OCR_PROCESSING_FAILED, details);
    }
}
