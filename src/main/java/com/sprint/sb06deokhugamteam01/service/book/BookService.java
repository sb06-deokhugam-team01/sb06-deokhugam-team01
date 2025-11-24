package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

    BookDto getBookById(String id);

    BookDto getBookByIsbn(String isbn);

    BookDto createBook(BookCreateRequest bookCreateRequest, @Nullable MultipartFile file);

    BookDto updateBook(BookUpdateRequest bookUpdateRequest, @Nullable MultipartFile file);

    void deleteBookById(String id);

    void hardDeleteBookById(String id);

}
