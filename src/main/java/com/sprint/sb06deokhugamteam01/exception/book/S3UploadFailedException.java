package com.sprint.sb06deokhugamteam01.exception.book;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;

import java.util.Map;

public class S3UploadFailedException extends RootException {
     public S3UploadFailedException(Map<String, Object> details) {
         super(ErrorCode.S3_UPLOAD_FAILED, details);
     }
}
