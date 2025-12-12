package com.sprint.sb06deokhugamteam01.service.book.coverage;

import com.sprint.sb06deokhugamteam01.exception.book.S3ObjectNotFound;
import com.sprint.sb06deokhugamteam01.service.book.BasicS3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BasicS3StorageServiceUnitTest {
    @Mock
    S3Client s3Client;
    @Mock
    S3Presigner s3Presigner;
    @InjectMocks
    BasicS3StorageService s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucket", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "presignedUrlExpiration", "PT10H");
    }

    @Test
    @DisplayName("putObject 테스트 - AWS mock 처리 후 서비스 로직만 테스트")
    void putObject() {
        // given
        String id = UUID.randomUUID().toString();
        byte[] data = "data".getBytes();

        // when
        s3Service.putObject(id, data);

        // then
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
    @Test
    @DisplayName("deleteObject 테스트 - AWS mock 처리 후 서비스 로직만 테스트")
    void deleteObject() {
        //given
        String id = UUID.randomUUID().toString();

        //when
        s3Service.deleteObject(id);

        //then
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("s3 파일 부재로 deleteObjects 실패 - AWS mock 처리 후 서비스 로직만 테스트")
    void deleteObjects_Failure_S3ObjectNotFound() {
        // given
        String id = UUID.randomUUID().toString();
        given(s3Client.headObject(any(Consumer.class)))
                .willThrow(NoSuchKeyException.builder().build());

        // when & then
        assertThrows(S3ObjectNotFound.class, () -> {s3Service.deleteObject(id);});
    }

    @Test
    @DisplayName("getPresignedUrl 테스트 - AWS mock 처리 후 서비스 로직만 테스트")
    void getPresignedUrl() throws Exception {
        // given
        String id = UUID.randomUUID().toString();
        PresignedGetObjectRequest request = mock(PresignedGetObjectRequest.class);
        given(request.url()).willReturn(new URL("http://url.com"));
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .willReturn(request);

        // when
        String result = s3Service.getPresignedUrl(id);

        // then
        assertEquals("http://url.com", result);
    }

    @Test
    @DisplayName("s3 파일 부재로 getPresignedUrl 실패 - AWS mock 처리 후 서비스 로직만 테스트")
    void getPresignedUrl_Failure_S3ObjectNotFound() {
        // given
        String id = UUID.randomUUID().toString();
        given(s3Client.headObject(any(Consumer.class)))
                .willThrow(NoSuchKeyException.builder().build());

        // when & then
        assertThrows(S3ObjectNotFound.class, () -> {s3Service.getPresignedUrl(id);});
    }
}
