package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceImpl 테스트")
class BookServiceImplTest {

    @InjectMocks
    private BookService bookService;

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
        BookDto bookDto = bookService.createBook(bookCreateRequest, null);

        //then
        assertNotNull(bookDto);
        assertEquals(bookCreateRequest.title(), bookDto.title());
        assertEquals(bookCreateRequest.author(), bookDto.author());
        assertEquals(bookCreateRequest.description(), bookDto.description());
        assertEquals(bookCreateRequest.publisher(), bookDto.publisher());
        assertEquals(bookCreateRequest.publishedDate(), bookDto.publishedDate());
        assertEquals(bookCreateRequest.isbn(), bookDto.isbn());

    }

    @Test
    @DisplayName("createBook 실패 테스트 - 이미 존재하는 ISBN")
    void createBook_Fail_AllReadyExistsIsbn() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("createBook 실패 테스트 - 잘못된 ISBN")
    void createBook_Fail_InvalidIsbn() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("createBook 실패 테스트 - 도서 정보 조회 불가")
    void createBook_Fail_CannotFetchBookInfo() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("createBook 실패 테스트 - S3 업로드 오류")
    void createBook_Fail_S3UploadError() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("updateBook 성공 테스트")
    void updateBook_Success() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("updateBook 실패 테스트 - 존재하지 않는 도서")
    void updateBook_Fail_NoSuchBook() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("updateBook 실패 테스트 - ISBN 변경 불가")
    void updateBook_Fail_CannotUpdateIsbn() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("deleteBookById 성공 테스트")
    void deleteBookById_Success() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("deleteBookById 실패 테스트 - 존재하지 않는 도서")
    void deleteBookById_Fail_NoSuchBook() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("hardDeleteBookById 성공 테스트")
    void hardDeleteBookById_Success() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("hardDeleteBookById 실패 테스트 - 존재하지 않는 도서")
    void hardDeleteBookById_Fail_NoSuchBook() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("paginateBooks 성공 테스트")
    void paginateBooks() {

        //given

        //when

        //then

    }

    @Test
    @DisplayName("paginateBooks 실패 테스트 - 잘못된 페이지 매개변수")
    void paginateBooks_Fail_InvalidPageParameters() {

        //given

        //when

        //then

    }

}