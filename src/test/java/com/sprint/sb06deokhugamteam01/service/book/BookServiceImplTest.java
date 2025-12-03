package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.exception.book.*;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDto bookDto;

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
        when(bookRepository.findById(bookDto.id()))
                .thenReturn(Optional.of(book));

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

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        //when
        NoSuchBookException e = assertThrows(NoSuchBookException.class, () -> {
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

        when(bookRepository.findByIsbn(isbn))
                .thenReturn(Optional.of(book));

        //when
        BookDto result = bookService.getBookByIsbn(isbn);

        //then
        assertNotNull(result);
        assertEquals(isbn, result.isbn());

    }

    @Test
    @DisplayName("getBookByIsbn 실패 테스트 - 존재하지 않는 도서")
    void getBookByIsbn_Fail_NoSuchBook() {

        //given
        String isbn = "0000000000000";

        when(bookRepository.findByIsbn(isbn))
                .thenReturn(Optional.empty());

        //when
        NoSuchBookException exception = assertThrows(NoSuchBookException.class, () -> {
            bookService.getBookByIsbn(isbn);
        });

        //then
        assertEquals("Book not found", exception.getMessage());

    }

    @Test
    @DisplayName("paginateBooks 성공 테스트")
    void paginateBooks_Success() {

        //given
        PagingBookRequest pagingBookRequest = PagingBookRequest.builder()
                .keyword("test")
                .orderBy(PagingBookRequest.OrderBy.valueOf("title".toUpperCase()))
                .direction(PagingBookRequest.SortDirection.ASC)
                .cursor("test-cursor")
                .after(LocalDateTime.now())
                .limit(10)
                .build();

        when(bookRepository.count())
                .thenReturn(100L);

        when(bookRepository.findBooksByKeyword(pagingBookRequest))
                .thenReturn(new SliceImpl<>(nCopies(11, book)));

        //when
        CursorPageResponseBookDto result = bookService.getBooksByPage(pagingBookRequest);

        //then
        assertNotNull(result);
        assertNotEquals(pagingBookRequest.limit(), result.getContent().size());

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

        when(bookRepository.existsByIsbn(bookCreateRequest.isbn()))
                .thenReturn(false);

        when(bookRepository.save(any(Book.class)))
                .thenReturn(book);

        //when
        BookDto result = bookService.createBook(bookCreateRequest, null);

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

        when(bookRepository.existsByIsbn(bookCreateRequest.isbn()))
                .thenReturn(true);

        //when
        AlreadyExistsIsbnException exception = assertThrows(AlreadyExistsIsbnException.class, () -> {
            bookService.createBook(bookCreateRequest, null);
        });

        //then
        assertEquals("All ready exists ISBN", exception.getMessage());

    }

    //외부 api 테스트이므로 현재는 실패
    @Test
    @DisplayName("createBook 실패 테스트 - 잘못된 ISBN")
    void createBook_Fail_InvalidIsbn() {

        //given
        BookCreateRequest bookCreateRequest = new BookCreateRequest(
                "9788966262084",
                "테스트 도서",
                "테스트 저자",
                "테스트 출판사",
                LocalDate.now(),
                "sdfadsfadsf"
        );

        //when
        InvalidIsbnException exception = assertThrows(InvalidIsbnException.class, () -> {
            bookService.createBook(bookCreateRequest, null);
        });

        //then
        assertEquals("유효하지 않은 ISBN 입니다.", exception.getMessage());

    }

    //외부 api 테스트이므로 현재는 실패
    @Test
    @DisplayName("createBook 실패 테스트 - 도서 정보 조회 불가")
    void createBook_Fail_CannotFetchBookInfo() {

        //given
        BookCreateRequest bookCreateRequest = new BookCreateRequest(
                "9788966262084",
                "테스트 도서",
                "테스트 저자",
                "테스트 출판사",
                LocalDate.now(),
                "sdfadsfadsf"
        );

        //when
        BookInfoFetchFailedException exception = assertThrows(BookInfoFetchFailedException.class, () -> {
            bookService.createBook(bookCreateRequest, null);
        });

        //then
        assertEquals("도서 정보를 조회할 수 없습니다.", exception.getMessage());

    }

    //외부 api 테스트이므로 현재는 실패
//    @Test
//    @DisplayName("createBook 실패 테스트 - S3 업로드 오류")
//    void createBook_Fail_S3UploadError() {
//
//        //given
//        BookCreateRequest bookCreateRequest = new BookCreateRequest(
//                "9788966262084",
//                "테스트 도서",
//                "테스트 저자",
//                "테스트 출판사",
//                LocalDate.now(),
//                "sdfadsfadsf"
//        );
//
//        //when
//        S3UploadFailedException exception = assertThrows(S3UploadFailedException.class, () -> {
//            bookService.createBook(bookCreateRequest, null);
//        });
//
//        //then
//        assertEquals("S3 업로드에 실패하였습니다.", exception.getMessage());
//
//    }

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

        Book updatedBook = BookUpdateRequest.fromDto(updateRequest);

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        //when
        BookDto result = bookService.updateBook(bookId, updateRequest, null);

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

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        //when
        NoSuchBookException exception = assertThrows(NoSuchBookException.class, () -> {
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
        when(bookRepository.findById(bookId))
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
        NoSuchBookException exception = assertThrows(NoSuchBookException.class, () -> {
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

        when(bookRepository.existsById(bookId))
                .thenReturn(true);

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
        NoSuchBookException exception = assertThrows(NoSuchBookException.class, () -> {
            bookService.hardDeleteBookById(bookId);
        });

        //then
        assertEquals("Book not found", exception.getMessage());

    }

}