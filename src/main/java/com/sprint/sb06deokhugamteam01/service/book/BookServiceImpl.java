package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class BookServiceImpl implements  BookService {

    @Override
    public BookDto getBookById(UUID id) {
        return null;
    }

    @Override
    public BookDto getBookByIsbn(String isbn) {
        return null;
    }

    @Override
    public CursorPageResponseBookDto getBooksByPage(PagingBookRequest pagingBookRequest) {
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
    public void deleteBookById(UUID id) {

    }

    @Override
    public void hardDeleteBookById(UUID id) {

    }
}
