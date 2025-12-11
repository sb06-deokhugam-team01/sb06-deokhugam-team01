package com.sprint.sb06deokhugamteam01.service.book.coverage;

import com.sprint.sb06deokhugamteam01.service.book.OcrSpaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class OcrSpaceServiceUnitTest {
    OcrSpaceService ocrSpaceService = new OcrSpaceService();

    @Test
    @DisplayName("가짜 키로 요청 시 API 호출 오류")
    void extractIsbn_Fail() {
        // given
        ReflectionTestUtils.setField(ocrSpaceService, "ocrSpaceApiEndpoint", "aaa");
        ReflectionTestUtils.setField(ocrSpaceService, "apiKey", "aaa");

        byte[] fakeImage = new byte[]{1, 2, 3};
        String fileType = "png";

        // when & then
        assertThrows(Exception.class, () -> {
            ocrSpaceService.extractIsbnFromImage(fakeImage, fileType);
        });
    }
}
