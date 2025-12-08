package com.sprint.sb06deokhugamteam01.controller;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.BookInfo;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("")
    public ResponseEntity<CursorPageResponseBookDto> getBooksByCursor(
            @Valid @ModelAttribute PagingBookRequest request
            ) {
        log.info("Received Book get request: keyword={}", request.keyword());
        CursorPageResponseBookDto response = bookService.getBooksByPage(request);
        log.info("Books retrieved successfully: {}", request.keyword());

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

    @PostMapping("/isbn/ocr")
    public ResponseEntity<String> getIsbnByImage(
            @RequestParam("image") MultipartFile image) {
        log.info("Received Book create by OCR ISBN request");
        String isbn = bookService.getIsbnByImage(image);
        log.info("Book created successfully: {}", isbn);

        return new ResponseEntity<>(isbn, HttpStatus.OK);

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
