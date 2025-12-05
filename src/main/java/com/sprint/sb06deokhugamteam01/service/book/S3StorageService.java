package com.sprint.sb06deokhugamteam01.service.book;

public interface S3StorageService {

    String putObject(String id, byte[] data);

    void deleteObject(String id);

    String getPresignedUrl(String id, String contentType);

}
