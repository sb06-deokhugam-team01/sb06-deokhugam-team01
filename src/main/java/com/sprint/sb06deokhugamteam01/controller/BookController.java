package com.sprint.sb06deokhugamteam01.controller;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.BookInfo;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.service.book.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("")
    public ResponseEntity<CursorPageResponseBookDto> getBooksByCursor(
            @RequestParam (required = false) String keyword,
            @RequestParam String orderBy,
            @RequestParam String direction,
            @RequestParam (required = false) String cursor,
            @RequestParam (required = false) String after,
            @RequestParam (required = false, defaultValue = "12") @Min(1) Integer limit
            ) {

        PagingBookRequest request = PagingBookRequest.builder()
                .keyword(keyword)
                .orderBy(PagingBookRequest.OrderBy.valueOf(orderBy.toUpperCase()))
                .direction(PagingBookRequest.SortDirection.valueOf(direction.toUpperCase()))
                .cursor(cursor)
                .after(after != null ? LocalDateTime.parse(after) : null)
                .limit(limit)
                .build();

        log.info("Received Book get request: keyword={}", request.keyword());
        CursorPageResponseBookDto response = bookService.getBooksByPage(request);
        log.info("Books retrieved successfully: {}", response);

        return ResponseEntity.ok(response);

    }

    @PostMapping("")
    public ResponseEntity<BookDto> createBook(
            @Valid @RequestPart("bookData") BookCreateRequest request,
            @RequestPart("thumbnailImage") MultipartFile thumbnailImage) {
        log.info("Received Book create request: isbn={}", request.author());
        BookDto createdBook = bookService.createBook(request, thumbnailImage);
        log.info("Book created successfully: {}", createdBook);

        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);

    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDto> getBookById(
            @PathVariable UUID bookId) {
        log.info("Received Book get request: bookId={}", bookId);
        BookDto bookDto = bookService.getBookById(bookId);
        log.info("Book retrieved successfully: {}", bookDto);

        return ResponseEntity.ok(bookDto);

    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBookById(
            @PathVariable UUID bookId) {
        log.info("Received Book delete request: bookId={}", bookId);
        bookService.deleteBookById(bookId);
        log.info("Book deleted successfully: bookId={}", bookId);

        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<BookDto> updateBookById(
            @PathVariable UUID bookId,
            @Valid @RequestPart("bookData") BookUpdateRequest request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {
        log.info("Received Book update request: bookId={}", bookId);
        BookDto updatedBook = bookService.updateBook(bookId, request, thumbnailImage);
        log.info("Book updated successfully: {}", updatedBook);

        return ResponseEntity.ok(updatedBook);

    }

    //TODO: 인기 도서 목록 조회

    @GetMapping("/info")
    public ResponseEntity<BookInfo> getBookByIsbn(
            @RequestParam String isbn) {
        log.info("Received Book get request: isbn={}", isbn);
        BookDto bookDto = bookService.getBookByIsbn(isbn);
        log.info("Book retrieved successfully: {}", bookDto);

        return ResponseEntity.ok(BookInfo.fromDto(bookDto));

    }

    @DeleteMapping("/{bookId}/hard")
    public ResponseEntity<Void> hardDeleteBookById(
            @PathVariable UUID bookId) {
        log.info("Received hard delete request for book: {}", bookId);
        bookService.hardDeleteBookById(bookId);
        log.info("Book hard deleted successfully: {}", bookId);

        return ResponseEntity.noContent().build();
    }

}
