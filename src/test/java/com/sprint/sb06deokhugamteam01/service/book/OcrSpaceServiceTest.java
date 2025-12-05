package com.sprint.sb06deokhugamteam01.service.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OcrSpaceService 테스트")
class OcrSpaceServiceTest {

    /*@Autowired
    private OcrSpaceService ocrSpaceService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("OCR 서비스 테스트")
    void testOcrService() throws IOException {

        //given
        ClassLoader classLoader = getClass().getClassLoader();
        File imageFile = new File(Objects.requireNonNull(classLoader.getResource("isbn.png")).getFile());
        String fileType = "PNG";
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());

        //when
        String isbn = ocrSpaceService.extractIsbnFromImage(fileContent, fileType);

        //then
        assertNotNull(isbn);
        assertEquals("978-88-97095-05-7", isbn);

    }*/



}