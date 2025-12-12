package com.sprint.sb06deokhugamteam01.repository.book.coverage;

import com.sprint.sb06deokhugamteam01.config.QueryDslConfig;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.book.BookOrderBy;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.repository.book.BookQRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {QueryDslConfig.class}
))
@ActiveProfiles("test")
public class BookQRepositoryTest {
    @Autowired
    private BookQRepository bookQRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        createBook("강아지 책", "강아지", LocalDate.now(), 5.0, 100);
        createBook("고양이 책", "고양이",  LocalDate.now().minusDays(1),3.0, 10);
        createBook("쥐 책", "쥐", LocalDate.now().minusDays(2),1.0, 0);
        entityManager.flush();
        entityManager.clear();
    }

    @ParameterizedTest
    @EnumSource(BookOrderBy.class)
    @DisplayName("모든 정렬 조건으로 조회 성공")
    void findAll(BookOrderBy orderBy) {
        // given
        PagingBookRequest request = new PagingBookRequest(
                null,
                orderBy.getFieldName(),
                SortDirection.ASC,
                null,
                null,
                10
        );

        // when
        Slice<Book> result = bookQRepository.findBooksByKeyword(request);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().size()).isEqualTo(3);
    }
    @Test
    @DisplayName("predicate 커버리지")
    void findByKeyword() {
        // given
        PagingBookRequest request = new PagingBookRequest(
                "강아지", BookOrderBy.TITLE.getFieldName(), SortDirection.ASC, null, null, 10
        );

        // when
        Slice<Book> result = bookQRepository.findBooksByKeyword(request);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("강아지 책");
    }

    private void createBook(String title, String author, LocalDate publishedDate, Double rating, int reviewCount){
        Book book = new Book(title, author, "설명", "출판사", publishedDate, "123-"+ title);
        ReflectionTestUtils.setField(book, "rating", rating);
        ReflectionTestUtils.setField(book, "reviewCount", reviewCount);
        ReflectionTestUtils.setField(book, "isActive", true);
        entityManager.persist(book);
    }
}
