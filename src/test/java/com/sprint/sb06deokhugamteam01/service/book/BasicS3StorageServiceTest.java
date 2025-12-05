package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.exception.book.S3ObjectNotFound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("BasicS3StorageService 테스트")
class BasicS3StorageServiceTest {

    @Autowired
    private BasicS3StorageService basicS3StorageService;

    @Test
    @DisplayName("putObject 성공 테스트")
    void putObject_Success() {

        //given
        String id = UUID.randomUUID().toString();
        byte[] content = "content".getBytes();

        //when
        String putResult = basicS3StorageService.putObject(id, content);

        //then
        assertEquals(id, putResult);
        basicS3StorageService.deleteObject(id);

    }

    @Test
    @DisplayName("deleteObject 성공 테스트")
    void deleteObject_Success() {

        //given
        String id = UUID.randomUUID().toString();
        byte[] content = "content".getBytes();
        String putResult = basicS3StorageService.putObject(id, content);

        //when

        //then
        assertDoesNotThrow(() -> basicS3StorageService.deleteObject(putResult));

    }

    @Test
    @DisplayName("deleteObjects 실패 테스트 - 객체 없음")
    void deleteObjects_Failure_S3ObjectNotFound() {

        //given
        String id = UUID.randomUUID().toString();

        //when
        S3ObjectNotFound exception = assertThrows(S3ObjectNotFound.class, () -> {
            basicS3StorageService.deleteObject(id);
        });

        //then
        assertEquals("S3 object not found", exception.getMessage());

    }

    @Test
    @DisplayName("getPresignedUrl 성공 테스트")
    void getPresignedUrl_Success() {

        //given
        String id = UUID.randomUUID().toString();
        byte[] content = "content".getBytes();
        String putResult = basicS3StorageService.putObject(id, content);

        //when
        String presignedUrl = basicS3StorageService.getPresignedUrl(putResult);

        //then
        assertNotNull(presignedUrl);
        basicS3StorageService.deleteObject(putResult);

    }

    @Test
    @DisplayName("getPresignedUrl 실패 테스트 - 객체 없음")
    void getPresignedUrl_Failure_S3ObjectNotFound() {

        //given
        String id = UUID.randomUUID().toString();

        //when
        S3ObjectNotFound exception = assertThrows(S3ObjectNotFound.class, () -> {
            basicS3StorageService.getPresignedUrl(id);
        });

        //then
        assertEquals("S3 object not found", exception.getMessage());
        
    }

}