package com.sprint.sb06deokhugamteam01.exception.book;

public class S3UploadFailedException extends RuntimeException {
    public S3UploadFailedException(String message) {
        super(message);
    }
}
