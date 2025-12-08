package com.sprint.sb06deokhugamteam01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.BookInfo;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.exception.book.BookNotFoundException;
import com.sprint.sb06deokhugamteam01.service.book.BookService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BookService bookService;
    BookDto bookDto;


    @BeforeEach
    void setUp() {

        bookDto = BookDto.builder()
                .id(UUID.randomUUID())
                .title("Sample Book Title")
                .author("Sample Author")
                .description("Sample Description")
                .publisher("Sample Publisher")
                .publishedDate(LocalDate.now())
                .isbn("1234567890")
                .thumbnailUrl("http://example.com/thumbnail.jpg")
                .build();

    }

    @Test
    @DisplayName("getBooksByCursor 성공 테스트")
    void getBooksByCursor_Success() throws Exception {

        //given
        EasyRandom easyRandom = new EasyRandom();
        PagingBookRequest request = PagingBookRequest.builder()
                .keyword("Java")
                .orderBy("publishedDate")
                .direction(PagingBookRequest.SortDirection.DESC)
                .cursor("")
                .after(LocalDateTime.now())
                .limit(10)
                .build();
        CursorPageResponseBookDto response = CursorPageResponseBookDto.builder()
                .content(new ArrayList<>())
                .nextCursor("next-cursor-value")
                .nextAfter("next-after-value")
                .size(10)
                .totalElements(100)
                .build();

        //when
        when(bookService.getBooksByPage(any(PagingBookRequest.class)))
            .thenReturn(response);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .param("keyword", request.keyword())
                        .param("orderBy", String.valueOf(request.orderBy()))
                        .param("direction", String.valueOf(request.direction()))
                        .param("cursor", request.cursor())
                        .param("after", String.valueOf(request.after()))
                        .param("limit", String.valueOf(request.limit()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.nextCursor").value(response.nextCursor()))
                .andExpect(jsonPath("$.nextAfter").value(response.nextAfter()))
                .andExpect(jsonPath("$.size").value(response.size()))
                .andExpect(jsonPath("$.totalElements").value(response.totalElements()));

    }

    @Test
    @DisplayName("getBooksByCursor 실패 테스트 - 잘못된 파라미터")
    void getBooksByCursor_Fail_InvalidParameters() throws Exception {

        //given
        String invalidLimit = "-5"; // 음수 값은 잘못된 파라미터
        PagingBookRequest request = PagingBookRequest.builder()
                .keyword("Java")
                .orderBy("publishedDate")
                .direction(PagingBookRequest.SortDirection.DESC)
                .cursor("")
                .after(LocalDateTime.now())
                .build();

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .param("keyword", request.keyword())
                        .param("orderBy", String.valueOf(request.orderBy()))
                        .param("direction", String.valueOf(request.direction()))
                        .param("cursor", request.cursor())
                        .param("after", String.valueOf(request.after()))
                        .param("limit", String.valueOf(invalidLimit))
                )
                .andExpect(status().isBadRequest());


    }

    @Test
    @DisplayName("createBook 성공 테스트")
    void createBook_Success() throws Exception {

        //given
        BookCreateRequest bookCreateRequest = BookCreateRequest.builder()
                .title("Sample Book Title")
                .author("Sample Author")
                .description("Sample Description")
                .publisher("Sample Publisher")
                .publishedDate(LocalDate.now())
                .isbn("1234567890")
                .build();
        MockMultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnailImage",
                "thumbnail.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "Sample Image Content".getBytes()
        );
        MockMultipartFile bookData = new MockMultipartFile(
                "bookData",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(bookCreateRequest)
        );

        //when
        when(bookService.createBook(any(BookCreateRequest.class), any(MultipartFile.class)))
                .thenReturn(bookDto);

        //then
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/books")
                        .file(bookData)
                        .file(thumbnailImage)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookDto.id().toString()))
                .andExpect(jsonPath("$.title").value(bookDto.title()))
                .andExpect(jsonPath("$.author").value(bookDto.author()))
                .andExpect(jsonPath("$.isbn").value(bookDto.isbn()));

    }

    @Test
    @DisplayName("createBook 실패 테스트 - 잘못된 입력 데이터")
    void createBook_Fail_InvalidInputData() throws Exception {

        //given
        BookCreateRequest invalidRequest = BookCreateRequest.builder()
                .title("") // 빈 제목은 잘못된 입력 데이터
                .author("Author Name")
                .description("Description")
                .publisher("Publisher")
                .publishedDate(LocalDate.EPOCH)
                .isbn("1234567890")
                .build();

        MockMultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnailImage",
                "thumbnail.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "Sample Image Content".getBytes()
        );
        MockMultipartFile bookData = new MockMultipartFile(
                "bookData",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(invalidRequest)
        );

        //then
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/books")
                        .file(bookData)
                        .file(thumbnailImage)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("createBookByIsbnImage 성공 테스트")
    void getIsbnByImage_Success() throws Exception {

        //given
        String isbn = "1234567890";
        MockMultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnailImage.jpg",
                "thumbnail.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "Sample Image Content".getBytes()
        );

        //when
        when(bookService.getIsbnByImage(any(MultipartFile.class)))
                .thenReturn(bookDto.isbn());

        //then
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/books/isbn/ocr")
                        .file("image", thumbnailImage.getBytes())
                )
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("getBookById 성공 테스트")
    void getBookById_Success() throws Exception {

        //given

        //when
        when(bookService.getBookById(bookDto.id()))
                .thenReturn(bookDto);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{bookId}", bookDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookDto.id().toString()))
                .andExpect(jsonPath("$.title").value(bookDto.title()))
                .andExpect(jsonPath("$.author").value(bookDto.author()))
                .andExpect(jsonPath("$.isbn").value(bookDto.isbn()));

    }

    @Test
    @DisplayName("getBookById 실패 테스트 - 존재하지 않는 도서")
    void getBookById_Fail_NoSuchBook() throws Exception {

        //given

        //when
        when(bookService.getBookById(bookDto.id()))
                .thenThrow(new BookNotFoundException(Map.of()));

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{bookId}", bookDto.id()))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("deleteBookById 성공 테스트")
    void deleteBookById_Success() throws Exception {

        //given
        String bookId = UUID.randomUUID().toString();

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{bookId}", bookId))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("getBookByIsbn 성공 테스트")
    void getBookByIsbn_Success() throws Exception {

        //given
        BookInfo bookInfo = BookInfo.builder()
                .isbn(bookDto.isbn())
                .title(bookDto.title())
                .author(bookDto.author())
                .build();

        //when
        when(bookService.getBookByIsbn(bookDto.isbn()))
                .thenReturn(bookDto);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/info")
                        .param("isbn", bookDto.isbn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(bookInfo.isbn()))
                .andExpect(jsonPath("$.title").value(bookInfo.title()))
                .andExpect(jsonPath("$.author").value(bookInfo.author()));

    }

    @Test
    @DisplayName("getBookByIsbn 실패 테스트 - 존재하지 않는 도서")
    void getBookByIsbn_Fail_NoSuchBook() throws Exception {

        //given

        //when
        when(bookService.getBookByIsbn(bookDto.isbn()))
                .thenThrow(new BookNotFoundException(Map.of()));

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/info")
                        .param("isbn", bookDto.isbn()))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("hardDeleteBookById 성공 테스트")
    void hardDeleteBookById_Success() throws Exception {

        //given
        String bookId = UUID.randomUUID().toString();

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{bookId}/hard", bookId))
                .andExpect(status().isNoContent());

    }


}