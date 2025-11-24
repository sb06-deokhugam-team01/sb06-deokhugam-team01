package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

public class BookServiceImpl implements  BookService {

    @Override
    public BookDto getBookById(String id) {
        return null;
    }

    @Override
    public BookDto getBookByIsbn(String isbn) {
        return null;
    }

    @Override
    public BookDto createBook(BookCreateRequest bookCreateRequest, @Nullable MultipartFile file) {
        return null;
    }

    @Override
    public BookDto updateBook(BookUpdateRequest bookUpdateRequest, @Nullable MultipartFile file) {
        return null;
    }

    @Override
    public void deleteBookById(String id) {

    }

    @Override
    public void hardDeleteBookById(String id) {

    }
}
