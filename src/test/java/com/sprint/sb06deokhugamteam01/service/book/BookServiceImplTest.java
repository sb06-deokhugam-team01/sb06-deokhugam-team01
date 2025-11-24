package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.exception.book.*;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceImpl 테스트")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("getBookById 성공 테스트")
    void getBookById_Success() {

        //given
        UUID bookId = UUID.randomUUID();

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(Book.builder()
                                .id(bookId)
                        .build()));

        //when
        BookDto result = bookService.getBookById(bookId);

        //then
        assertNotNull(result);
        assertEquals(bookId, result.id());

    }

    @Test
    @DisplayName("getBookById 실패 테스트 - 존재하지 않는 도서")
    void getBookById_Fail_NoSuchBook() {

        //given
        UUID bookId = UUID.randomUUID();

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        //when
        NoSuchBookException exception = assertThrows(NoSuchBookException.class, () -> {
            bookService.getBookById(bookId);
        });

        //then
        assertEquals("존재하지 않는 도서입니다.", exception.getMessage());

    }

    @Test
    @DisplayName("getBookByIsbn 성공 테스트")
    void getBookByIsbn_Success() {

        //given
        String isbn = "9788966262084";

        when(bookRepository.findByIsbn(isbn))
                .thenReturn(Optional.of(Book.builder()
                        .isbn(isbn)
                        .build()));

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
        assertEquals("존재하지 않는 도서입니다.", exception.getMessage());

    }

    @Test
    @DisplayName("paginateBooks 성공 테스트")
    void paginateBooks_Success() {

        //given
        PagingBookRequest pagingBookRequest = PagingBookRequest.builder()
                .keyword("test")
                .orderBy("title")
                .direction("asc")
                .cursor("test-cursor")
                .after(LocalDateTime.now())
                .limit(10)
                .build();

        //when
        CursorPageResponseBookDto result = bookService.getBooksByPage(pagingBookRequest);

        //then
        assertNotNull(result);
        assertNotEquals(0, result.getContent().size());

    }

    @Test
    @DisplayName("createBook 성공 테스트")
    void createBook_Success() {

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

        when(bookRepository.existsByIsbn("9788966262084"))
                .thenReturn(true);


        //when
        AllReadyExistsIsbnException exception = assertThrows(AllReadyExistsIsbnException.class, () -> {
            bookService.createBook(bookCreateRequest, null);
        });

        //then
        assertEquals("이미 존재하는 ISBN 입니다.", exception.getMessage());

    }

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

        //when
        S3UploadFailedException exception = assertThrows(S3UploadFailedException.class, () -> {
            bookService.createBook(bookCreateRequest, null);
        });

        //then
        assertEquals("S3 업로드에 실패하였습니다.", exception.getMessage());

    }

    @Test
    @DisplayName("updateBook 성공 테스트")
    void updateBook_Success() {

        //given
        String bookId = "existing-book-id";
        BookUpdateRequest updateRequest = BookUpdateRequest.builder()
                .title("수정된 도서 제목")
                .author("수정된 저자")
                .description("수정된 설명")
                .publisher("수정된 출판사")
                .publishedDate(LocalDate.now())
                .build();

        BookDto expectedDto = BookDto.builder()
                .id(bookId)
                .title(updateRequest.title())
                .author(updateRequest.author())
                .description(updateRequest.description())
                .publisher(updateRequest.publisher())
                .publishedDate(updateRequest.publishedDate())
                .build();

        //when
        BookDto result = bookService.updateBook(updateRequest, null);

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
        String bookId = "existing-book-id";
        BookUpdateRequest updateRequest = BookUpdateRequest.builder()
                .title("수정된 도서 제목")
                .author("수정된 저자")
                .description("수정된 설명")
                .publisher("수정된 출판사")
                .publishedDate(LocalDate.now())
                .build();

        when(bookRepository.existsById(bookId))
                .thenReturn(false);

        //when
        BookInfoFetchFailedException exception = assertThrows(BookInfoFetchFailedException.class, () -> {
            bookService.updateBook(updateRequest, null);
        });

        //then
        assertEquals("존재하지 않는 도서입니다.", exception.getMessage());

    }

    @Test
    @DisplayName("deleteBookById 성공 테스트")
    void deleteBookById_Success() {

        //given
        UUID bookId = UUID.randomUUID();

        //when

        //then
        assertDoesNotThrow(() -> {
            bookService.deleteBookById(bookId);
        });

    }

    @Test
    @DisplayName("deleteBookById 실패 테스트 - 존재하지 않는 도서")
    void deleteBookById_Fail_NoSuchBook() {

        //given
        UUID bookId = UUID.randomUUID();

        //when
        NoSuchBookException exception = assertThrows(NoSuchBookException.class, () -> {
            bookService.deleteBookById(bookId);
        });

        //then
        assertEquals("존재하지 않는 도서입니다.", exception.getMessage());

    }

    @Test
    @DisplayName("hardDeleteBookById 성공 테스트")
    void hardDeleteBookById_Success() {

        //given
        UUID bookId = UUID.randomUUID();

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
        UUID bookId = UUID.randomUUID();

        //when
        NoSuchBookException exception = assertThrows(NoSuchBookException.class, () -> {
            bookService.hardDeleteBookById(bookId);
        });

        //then
        assertEquals("존재하지 않는 도서입니다.", exception.getMessage());

    }

}