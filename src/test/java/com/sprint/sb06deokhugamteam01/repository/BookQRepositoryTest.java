package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.config.QueryDslConfig;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
@DisplayName("BookQRepository 테스트")
class BookQRepositoryTest {

    @Autowired
    private BookQRepository bookQRepository;

    @BeforeEach
    void setUp() {
        bookQRepository.deleteAll();
    }

    @Test
    @DisplayName("BookQRepository 페이징 조회 테스트")
    void testBookQRepositoryPaging() {

        //given
        List<Book> bookList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Book book = Book.builder()
                    .title("Test Book " + i)
                    .author("Author " + i)
                    .isbn("ISBN-" + UUID.randomUUID())
                    .publishedDate(LocalDate.now())
                    .publisher("Publisher " + (i % 5))
                    .build();
            bookList.add(book);
        }
        bookQRepository.saveAll(bookList);

        PagingBookRequest request = PagingBookRequest.builder()
                .keyword("Test Book 1")
                .orderBy("title")
                .direction(PagingBookRequest.SortDirection.DESC)
                .cursor(bookList.get(10).getTitle())
                .after(LocalDateTime.now().minusDays(1))
                .limit(50)
                .build();

        //when
        Slice<Book> books = bookQRepository.findBooksByKeyword(request);

        //then
        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertTrue(books.getContent().size() <= 50);

    }

}