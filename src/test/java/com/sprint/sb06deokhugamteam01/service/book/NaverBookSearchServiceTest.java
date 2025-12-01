package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NaverBookSearchService 테스트")
class NaverBookSearchServiceTest {

    @InjectMocks
    private NaverBookSearchService naverBookSearchService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("ISBN으로 도서 검색 테스트")
    void testSearchBookByIsbn() {

        String testIsbn = "9791155976043"; // 예시 ISBN
        BookDto book = naverBookSearchService.searchBookByIsbn(testIsbn);

        assertNotNull(book);
        assertEquals(testIsbn, book.isbn());

    }

}