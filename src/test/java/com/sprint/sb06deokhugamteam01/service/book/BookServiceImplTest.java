package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.exception.book.AlreadyExistsIsbnException;
import com.sprint.sb06deokhugamteam01.exception.book.BookNotFoundException;
import com.sprint.sb06deokhugamteam01.exception.book.S3UploadFailedException;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchBookRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceImpl 테스트")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookSearchService bookSearchService;

    @Mock
    private BatchBookRatingRepository batchBookRatingRepository;

    @Mock
    private OcrService ocrService;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDto bookDto;

    @Value("naver.books.api-key")
    private String apiKey;

    @BeforeEach
    void setUp() {

        EasyRandom easyRandom = new EasyRandom();

        book = easyRandom.nextObject(Book.class);
        bookDto = BookDto.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .thumbnailUrl(book.getThumbnailUrl())
                .build();

    }

    @Test
    @DisplayName("getBookById 성공 테스트")
    void getBookById_Success() {

        //given
        when(bookRepository.findByIdAndIsActive(bookDto.id(), true))
                .thenReturn(Optional.of(book));

        when(s3StorageService.getPresignedUrl(bookDto.thumbnailUrl()))
                .thenReturn(bookDto.thumbnailUrl());

        //when
        BookDto result = bookService.getBookById(bookDto.id());

        //then
        assertNotNull(result);
        assertEquals(bookDto.id(), result.id());

    }

    @Test
    @DisplayName("getBookById 실패 테스트 - 존재하지 않는 도서")
    void getBookById_Fail_NoSuchBook() {

        //given
        UUID bookId = UUID.randomUUID();

        when(bookRepository.findByIdAndIsActive(bookId, true))
                .thenReturn(Optional.empty());

        //when
        BookNotFoundException e = assertThrows(BookNotFoundException.class, () -> {
            bookService.getBookById(bookId);
        });

        //then
        assertEquals("Book not found", e.getMessage());

    }

    @Test
    @DisplayName("getBookByIsbn 성공 테스트")
    void getBookByIsbn_Success() {

        //given
        String isbn = bookDto.isbn();

        when(bookSearchService.searchBookByIsbn(isbn))
                .thenReturn(bookDto);

        //when
        BookDto result = bookService.getBookByIsbn(isbn);

        //then
        assertNotNull(result);
        assertEquals(isbn, result.isbn());

    }

    @Test
    @DisplayName("paginateBooks 성공 테스트")
    void paginateBooks_Success() {

        //given
        PagingBookRequest pagingBookRequest = PagingBookRequest.builder()
                .keyword("test")
                .orderBy("title")
                .direction(PagingBookRequest.SortDirection.ASC)
                .cursor("test-cursor")
                .after(LocalDateTime.now())
                .limit(10)
                .build();

        when(bookRepository.count())
                .thenReturn(100L);

        when(bookRepository.findBooksByKeyword(pagingBookRequest))
                .thenReturn(new SliceImpl<>(nCopies(11, book)));

        when(s3StorageService.getPresignedUrl(any(String.class)))
                .thenReturn(bookDto.thumbnailUrl());

        //when
        CursorPageResponseBookDto result = bookService.getBooksByPage(pagingBookRequest);

        //then
        assertNotNull(result);
        assertNotEquals(pagingBookRequest.limit(), result.content().size());

    }

    @Test
    @DisplayName("createBook 성공 테스트")
    void createBook_Success() {

        //given
        BookCreateRequest bookCreateRequest = new BookCreateRequest(
                bookDto.title(),
                bookDto.author(),
                bookDto.description(),
                bookDto.publisher(),
                bookDto.publishedDate(),
                bookDto.isbn()
        );

        MockMultipartFile mockThumbnail = new MockMultipartFile("thumbnail.png", "thumbnail.png", "image/png", new byte[]{1,2,3});

        when(bookRepository.existsByIsbnAndIsActive(bookCreateRequest.isbn(), true))
                .thenReturn(false);

        when(bookRepository.save(any(Book.class)))
                .thenReturn(book);

        //when
        BookDto result = bookService.createBook(bookCreateRequest, mockThumbnail);

        //then
        assertNotNull(result);
        assertEquals(bookCreateRequest.title(), result.title());
        assertEquals(bookCreateRequest.author(), result.author());
        assertEquals(bookCreateRequest.description(), result.description());
        assertEquals(bookCreateRequest.publisher(), result.publisher());
        assertEquals(bookCreateRequest.publishedDate(), result.publishedDate());
        assertEquals(bookCreateRequest.isbn(), result.isbn());

    }

    @Test
    @DisplayName("createBook 실패 테스트 - 이미 존재하는 ISBN")
    void createBook_Fail_AllReadyExistsIsbn() {

        //given
        BookCreateRequest bookCreateRequest = new BookCreateRequest(
                "9788966262084",
                "테스트 도서",
                "테스트 저자",
                "테스트 출판사",
                LocalDate.now(),
                "sdfadsfadsf"
        );

        when(bookRepository.existsByIsbnAndIsActive(bookCreateRequest.isbn(), true))
                .thenReturn(true);

        //when
        AlreadyExistsIsbnException exception = assertThrows(AlreadyExistsIsbnException.class, () -> {
            bookService.createBook(bookCreateRequest, null);
        });

        //then
        assertEquals("Already exists ISBN", exception.getMessage());

    }

    //외부 api 테스트이므로 현재는 실패
    @Test
    @DisplayName("createBook 실패 테스트 - S3 업로드 오류")
    void createBook_Fail_S3UploadError() {

        //given
        BookCreateRequest bookCreateRequest = new BookCreateRequest(
                "9788966262084",
                "테스트 도서",
                "테스트 저자",
                "테스트 출판사",
                LocalDate.now(),
                "sdfadsfadsf"
        );

        MockMultipartFile mockThumbnail = new MockMultipartFile("thumbnail.png", "thumbnail.png", "image/png", new byte[]{1,2,3});

        when(bookRepository.existsByIsbnAndIsActive(bookCreateRequest.isbn(), true))
                .thenReturn(false);

        when(s3StorageService.putObject(anyString(), any(byte[].class)))
                .thenThrow(new S3UploadFailedException(new HashMap<>()));

        //when
        S3UploadFailedException exception = assertThrows(S3UploadFailedException.class, () -> {
            bookService.createBook(bookCreateRequest, mockThumbnail);
        });

        //then
        assertEquals("S3 upload failed", exception.getMessage());

    }

    @Test
    @DisplayName("getIsbnByImage 성공 테스트")
    void getIsbnByImage_Success() {

        //given
        String isbn = "9788966262084";

        when(ocrService.extractIsbnFromImage(any(byte[].class), any(String.class)))
                .thenReturn(isbn);

        //when
        String result = bookService.getIsbnByImage(new MockMultipartFile("isbnImage.png", "isbnImage.png", "png",new byte[]{}));

        //then
        assertNotNull(result);
        assertEquals(isbn, result);

    }

    @Test
    @DisplayName("updateBook 성공 테스트")
    void updateBook_Success() {

        //given
        UUID bookId = bookDto.id();
        BookUpdateRequest updateRequest = BookUpdateRequest.builder()
                .title("수정된 도서 제목")
                .author("수정된 저자")
                .description("수정된 설명")
                .publisher("수정된 출판사")
                .publishedDate(LocalDate.now())
                .build();
        MockMultipartFile mockThumbnail = new MockMultipartFile("thumbnail.png", "thumbnail.png", "image/png", new byte[]{1,2,3});

        Book updatedBook = BookUpdateRequest.fromDto(updateRequest);

        when(bookRepository.findByIdAndIsActive(bookId, true))
                .thenReturn(Optional.of(book));

        //when
        BookDto result = bookService.updateBook(bookId, updateRequest, mockThumbnail);

        //then
        assertNotNull(result);
        assertEquals(bookId, result.id());
        assertEquals(updateRequest.title(), result.title());
        assertEquals(updateRequest.author(), result.author());
        assertEquals(updateRequest.description(), result.description());
        assertEquals(updateRequest.publisher(), result.publisher());
        assertEquals(updateRequest.publishedDate(), result.publishedDate());

    }

    @Test
    @DisplayName("updateBook 실패 테스트 - 존재하지 않는 도서")
    void updateBook_Fail_NoSuchBook() {

        //given
        UUID bookId = bookDto.id();
        BookUpdateRequest updateRequest = BookUpdateRequest.builder()
                .title("수정된 도서 제목")
                .author("수정된 저자")
                .description("수정된 설명")
                .publisher("수정된 출판사")
                .publishedDate(LocalDate.now())
                .build();

        when(bookRepository.findByIdAndIsActive(bookId, true))
                .thenReturn(Optional.empty());

        //when
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.updateBook(bookDto.id(), updateRequest, null);
        });

        //then
        assertEquals("Book not found", exception.getMessage());

    }

    @Test
    @DisplayName("deleteBookById 성공 테스트")
    void deleteBookById_Success() {

        //given
        UUID bookId = bookDto.id();

        //when
        when(bookRepository.findByIdAndIsActive(bookId, true))
                .thenReturn(Optional.of(book));

        //then
        assertDoesNotThrow(() -> {
            bookService.deleteBookById(bookId);
        });

    }

    @Test
    @DisplayName("deleteBookById 실패 테스트 - 존재하지 않는 도서")
    void deleteBookById_Fail_NoSuchBook() {

        //given
        UUID bookId = bookDto.id();

        //when
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBookById(bookId);
        });

        //then
        assertEquals("Book not found", exception.getMessage());

    }

    @Test
    @DisplayName("hardDeleteBookById 성공 테스트")
    void hardDeleteBookById_Success() {

        //given
        UUID bookId = bookDto.id();

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.ofNullable(book));

        when(reviewRepository.findByBook_Id(bookId))
                .thenReturn(emptyList());

        //when

        //then
        assertDoesNotThrow(() -> {
            bookService.hardDeleteBookById(bookId);
        });

    }

    @Test
    @DisplayName("hardDeleteBookById 실패 테스트 - 존재하지 않는 도서")
    void hardDeleteBookById_Fail_NoSuchBook() {

        //given
        UUID bookId = bookDto.id();

        //when
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.hardDeleteBookById(bookId);
        });

        //then
        assertEquals("Book not found", exception.getMessage());

    }

}