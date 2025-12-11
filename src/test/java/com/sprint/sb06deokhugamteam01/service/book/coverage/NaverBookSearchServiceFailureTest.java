package com.sprint.sb06deokhugamteam01.service.book.coverage;

import com.sprint.sb06deokhugamteam01.exception.book.BookInfoFetchFailedException;
import com.sprint.sb06deokhugamteam01.service.book.NaverBookSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class NaverBookSearchServiceFailureTest {

    @Autowired
    NaverBookSearchService naverBookSearchService;

    @Test
    @DisplayName("가짜 키로 요청할 시 도서 검색 실패")
    void searchBookByIsbn() {
        // given
        String isbn = "1234567890";

        // when & then
        assertThrows(BookInfoFetchFailedException.class, () -> {
            naverBookSearchService.searchBookByIsbn(isbn);
        });
    }
}
