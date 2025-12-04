package com.sprint.sb06deokhugamteam01.service.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicS3StorageService 테스트")
class BasicS3StorageServiceTest {

    @Test
    @DisplayName("putObject 성공 테스트")
    void putObject_Success() {

    }

    @Test
    @DisplayName("deleteObject 성공 테스트")
    void deleteObject_Success() {

    }

    @Test
    @DisplayName("deleteObjects 실패 테스트 - 객체 없음")
    void deleteObjects_Failure_S3ObjectNotFound() {

    }

    @Test
    @DisplayName("getPresignedUrl 성공 테스트")
    void getPresignedUrl_Success() {

    }

    @Test
    @DisplayName("getPresignedUrl 실패 테스트 - 객체 없음")
    void getPresignedUrl_Failure_S3ObjectNotFound() {
        
    }

}