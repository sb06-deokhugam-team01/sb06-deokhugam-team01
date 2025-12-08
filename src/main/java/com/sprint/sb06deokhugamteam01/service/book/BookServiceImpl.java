package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.book.BookOrderBy;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.PopularBookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingPopularBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPopularPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.exception.book.AlreadyExistsIsbnException;
import com.sprint.sb06deokhugamteam01.exception.book.InvalidIsbnException;
import com.sprint.sb06deokhugamteam01.exception.book.BookNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchBookRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.book.PopularBookQRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchBookRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements  BookService {

    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final PopularBookQRepository popularBookQRepository;
    private final BatchBookRatingRepository batchBookRatingRepository;
    private final BookSearchService bookSearchService;
    private final OcrService ocrService;

    @Override
    public BookDto getBookById(UUID id) {
        return BookDto.fromEntity(bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(detailMap("id", id))));
    }

    @Override
    public BookDto getBookByIsbn(String isbn) {
        return bookSearchService.searchBookByIsbn(isbn.replace("-", ""));
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPageResponseBookDto getBooksByPage(PagingBookRequest pagingBookRequest) {

        Slice<Book> bookSlice = bookRepository.findBooksByKeyword(pagingBookRequest);

        return CursorPageResponseBookDto.builder()
                .content(bookSlice.getContent().stream()
                        .map(BookDto::fromEntity)
                        .limit(bookSlice.getContent().size() - (bookSlice.hasNext() ? 1 : 0))
                        .toList())
                .nextCursor(bookSlice.hasNext() ?
                        switch (BookOrderBy.withFieldName(pagingBookRequest.orderBy())) {
                            case TITLE -> bookSlice.getContent().get(bookSlice.getContent().size() -1).getTitle();
                            case PUBLISHED_DATE -> bookSlice.getContent().get(bookSlice.getContent().size() -1).getPublishedDate().toString();
                            case RATING -> String.valueOf(bookSlice.getContent().get(bookSlice.getContent().size() -1).getRating());
                            case REVIEW_COUNT -> String.valueOf(bookSlice.getContent().get(bookSlice.getContent().size() -1).getReviewCount());
                        } : null)
                .nextAfter(bookSlice.hasNext() ?
                        bookSlice.getContent().get(bookSlice.getContent().size() -1).getCreatedAt().toString() : null)
                .size(pagingBookRequest.limit())
                .totalElements((int) bookRepository.count())
                .hasNext(bookSlice.hasNext())
                .build();

    }

    @Transactional(readOnly = true)
    @Override
    public CursorPopularPageResponseBookDto getBooksByPopularPage(PagingPopularBookRequest pagingPopularBookRequest) {

        Slice<BatchBookRating> bookSlice = popularBookQRepository.findPopularBooksByPeriodAndCursor(pagingPopularBookRequest);

        return CursorPopularPageResponseBookDto.builder()
                .content(bookSlice.getContent().stream().map(
                        batchBookRating -> PopularBookDto.fromEntity(
                                batchBookRating.getBook(),
                                batchBookRating
                )).toList())
                .nextCursor(bookSlice.hasNext() ?
                        bookSlice.getContent().get(bookSlice.getContent().size() -1).getBook().getId().toString() : null)
                .nextAfter(bookSlice.hasNext() ?
                        bookSlice.getContent().get(bookSlice.getContent().size() -1).getBook().getCreatedAt().toString() : null)
                .size(pagingPopularBookRequest.limit())
                .totalElements((int) bookRepository.count())
                .hasNext(bookSlice.hasNext())
                .build();

    }

    @Transactional
    @Override
    public BookDto createBook(BookCreateRequest bookCreateRequest, @Nullable MultipartFile file) {

        if (bookRepository.existsByIsbn(bookCreateRequest.isbn()) && bookRepository.findByIsbn(bookCreateRequest.isbn()).get().isActive()) {
            throw new AlreadyExistsIsbnException(detailMap("isbn", bookCreateRequest.isbn()));
        }

        Book book = BookCreateRequest.fromDto(bookCreateRequest);

        //ToDo: S3 파일 업로드 처리

        return BookDto.fromEntity(bookRepository.save(book));

    }

    @Transactional
    @Override
    public String getIsbnByImage(MultipartFile image) {

        String isbn = null;
        try {
            isbn = ocrService.extractIsbnFromImage(image.getBytes(), image.getOriginalFilename().split("\\.")[1]);
        } catch (IOException e) {
            throw new InvalidIsbnException(new HashMap<>());
        }

        return isbn;

    }

    @Transactional
    @Override
    public BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest, @Nullable MultipartFile file) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(detailMap("id", id)));

        if (!book.isActive()) {
            throw new BookNotFoundException(detailMap("id", id));
        }

        book.updateBook(
                bookUpdateRequest.title(),
                bookUpdateRequest.author(),
                bookUpdateRequest.description(),
                bookUpdateRequest.publisher(),
                bookUpdateRequest.publishedDate()
        );

        return BookDto.fromEntity(book);

    }

    @Transactional
    @Override
    public void deleteBookById(UUID id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(detailMap("id", id)));

        if (!book.isActive()) {
            throw new BookNotFoundException(detailMap("id", id));
        }

        book.softDelete();

    }

    @Transactional
    @Override
    public void hardDeleteBookById(UUID id) {

        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(detailMap("id", id));
        }

        batchBookRatingRepository.deleteByBook_Id(id);

        //연관관계 매핑된 리뷰들 모두 삭제하기
        List<Review> reviewList = reviewRepository.findByBook_Id(id);
        commentRepository.deleteByReviewIn(reviewList);
        reviewRepository.deleteByBook_Id(id);

        bookRepository.deleteById(id);

    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }

}
