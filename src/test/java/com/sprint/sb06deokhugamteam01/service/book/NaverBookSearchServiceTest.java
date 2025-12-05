package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.exception.book.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("NaverBookSearchService 통합 테스트")
class NaverBookSearchServiceTest {

    /*@Autowired
    private NaverBookSearchService naverBookSearchService;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("환경 변수 주입 테스트")
    void testEnvironmentVariables() {
        //then
        assertNotNull(clientId);
        assertNotNull(clientSecret);
        assertFalse(clientId.isEmpty(), "Client ID should not be empty");
        assertFalse(clientSecret.isEmpty(), "Client Secret should not be empty");
    }

    @Test
    @DisplayName("ISBN으로 도서 검색 테스트")
    void testSearchBookByIsbn() {

        //given
        String testIsbn = "9791155976043"; // 예시 ISBN

        //when
        BookDto book = naverBookSearchService.searchBookByIsbn(testIsbn);

        //then
        assertNotNull(book);
        assertEquals(testIsbn, book.isbn());

    }

    @Test
    @DisplayName("도서 검색 결과 없음")
    void testSearchBookByIsbn_Fail_BookNotFoundException() {

        //given
        String invalidIsbn = "0000000000000"; // 존재하지 않는 ISBN

        //when

        //then
        assertThrows(BookNotFoundException.class, () -> {
            naverBookSearchService.searchBookByIsbn(invalidIsbn);
        });

    }*/

}