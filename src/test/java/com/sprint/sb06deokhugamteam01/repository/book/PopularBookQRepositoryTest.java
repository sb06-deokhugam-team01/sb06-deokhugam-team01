package com.sprint.sb06deokhugamteam01.repository.book;

import com.sprint.sb06deokhugamteam01.config.QueryDslConfig;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.dto.book.PopularBookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingPopularBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPopularPageResponseBookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
@DisplayName("PopularBookQRepository 테스트")
class PopularBookQRepositoryTest {

    @Autowired
    private PopularBookQRepository popularBookQRepository;

    @Autowired
    private BookQRepository bookQRepository;

    @BeforeEach
    void setUp() {
        bookQRepository.deleteAll();
        popularBookQRepository.deleteAll();
    }

    @Test
    @DisplayName("PopularBookQRepository 커서 기반 페이지네이션 테스트")
    void testBookQRepositoryCursorPagination() {

        //given
        List<Book> bookList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Book book = Book.builder()
                    .title("Cursor Test Book " + i)
                    .author("Cursor Author " + i)
                    .isbn("CURSOR-ISBN-" + UUID.randomUUID())
                    .publishedDate(LocalDate.now())
                    .publisher("Cursor Publisher " + (i % 3))
                    .build();
            bookList.add(book);
        }

        bookQRepository.saveAll(bookList);
        bookList = bookQRepository.findAll();

        List<BatchBookRating> batchBookRatingList = new ArrayList<>();
        for (Book book : bookList) {
            BatchBookRating rating = BatchBookRating.builder()
                    .book(book)
                    .periodType(PeriodType.ALL_TIME)
                    .periodStart(LocalDate.now().minusWeeks(1))
                    .periodEnd(LocalDate.now())
                    .avgRating(4.0)
                    .score(100)
                    .rank(1)
                    .createdAt(LocalDate.now().atStartOfDay())
                    .build();
            batchBookRatingList.add(rating);
        }

        popularBookQRepository.saveAll(batchBookRatingList);

        //when
        Slice<BatchBookRating> slice = popularBookQRepository.findPopularBooksByPeriodAndCursor(
                PagingPopularBookRequest.builder()
                        .period(PeriodType.ALL_TIME)
                        .direction(PagingPopularBookRequest.SortDirection.DESC)
                        .cursor(null)
                        .after(null)
                        .limit(10)
                        .build()
        );

        //then
        assertNotNull(slice);
        assertFalse(slice.isEmpty());
        System.out.println(slice.getContent().size());
        assertTrue(slice.getContent().size() == 11);

    }

}