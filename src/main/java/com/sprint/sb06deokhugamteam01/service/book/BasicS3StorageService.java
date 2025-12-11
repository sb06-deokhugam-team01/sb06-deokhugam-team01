package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.exception.book.S3DeleteFailedException;
import com.sprint.sb06deokhugamteam01.exception.book.S3ObjectNotFound;
import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicS3StorageService implements S3StorageService {

    @Value("${spring.cloud.aws.s3.presigned-url-expiration}")
    private String presignedUrlExpiration;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public String putObject(String id, byte[] data) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(id)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

        log.info("Success to put object to S3 with id: {}", id);

        return id;

    }

    @Override
    public void deleteObject(String id) {

        checkIfObjectExists(id);

        try {

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(id)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Success to delete object from S3 with id: {}", id);

        } catch (Exception e) {
            log.error("Failed to delete object from S3 with id: {}", id, e);

            throw new S3DeleteFailedException(detailMap("id", id));
        }

    }

    @Override
    public String getPresignedUrl(String id) {

        checkIfObjectExists(id);

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.parse(presignedUrlExpiration))
                .getObjectRequest(builder -> builder.bucket(bucket)
                        .key(id)
                )
                .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest)
                .url().toString();
    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }

    private void checkIfObjectExists(String id) {
        try {
            s3Client.headObject(b -> b.bucket(bucket).key(id));
        } catch (NoSuchKeyException | S3Exception e) {
            throw new S3ObjectNotFound(detailMap("id", id));
        }
    }

}
