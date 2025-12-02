package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface BookService {

    BookDto getBookById(UUID id);

    BookDto getBookByIsbn(String isbn);

    CursorPageResponseBookDto getBooksByPage(PagingBookRequest pagingBookRequest);

    BookDto createBook(BookCreateRequest bookCreateRequest, @Nullable MultipartFile file);

    BookDto createBookByIsbn(String isbn);

    BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest, @Nullable MultipartFile file);

    void deleteBookById(UUID id);

    void hardDeleteBookById(UUID id);

}
