package com.sprint.sb06deokhugamteam01.service.book.coverage;

import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingPopularBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPopularPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchBookRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchReviewRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.book.PopularBookQRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import com.sprint.sb06deokhugamteam01.service.book.BookSearchService;
import com.sprint.sb06deokhugamteam01.service.book.BookServiceImpl;
import com.sprint.sb06deokhugamteam01.service.book.OcrService;
import com.sprint.sb06deokhugamteam01.service.book.S3StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingPopularBookRequest.SortDirection;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock BookRepository bookRepository;
    @Mock CommentRepository commentRepository;
    @Mock ReviewRepository reviewRepository;
    @Mock PopularBookQRepository popularBookQRepository;
    @Mock BatchBookRatingRepository batchBookRatingRepository;
    @Mock BatchReviewRatingRepository batchReviewRatingRepository;
    @Mock BookSearchService bookSearchService;
    @Mock OcrService ocrService;
    @Mock S3StorageService s3StorageService;

    @Test
    @DisplayName("인기 도서 목록 조회")
    void getBooksByPopularPage() {
        // given
        Book book = Book.builder().title("인기 책").build();
        ReflectionTestUtils.setField(book, "id", UUID.randomUUID());
        book.updateThumbnailUrl("url");
        BatchBookRating rating = BatchBookRating.builder()
                .book(book).id(UUID.randomUUID()).rank(1).build();

        Slice<BatchBookRating> mockSlice = new SliceImpl<>(List.of(rating));
        given(popularBookQRepository.findPopularBooksByPeriodAndCursor(any())).willReturn(mockSlice);
        given(s3StorageService.getPresignedUrl(any())).willReturn("http://url");

        PagingPopularBookRequest request = new PagingPopularBookRequest(PeriodType.WEEKLY,
                SortDirection.DESC, null, null, 10);

        // when
        CursorPopularPageResponseBookDto result = bookService.getBooksByPopularPage(request);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("인기 책");
    }

}
