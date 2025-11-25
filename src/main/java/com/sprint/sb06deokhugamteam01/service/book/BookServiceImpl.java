package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.exception.book.AllReadyExistsIsbnException;
import com.sprint.sb06deokhugamteam01.exception.book.NoSuchBookException;
import com.sprint.sb06deokhugamteam01.mapper.book.BookMapper;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements  BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto getBookById(UUID id) {
        return bookMapper.toDto(bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchBookException("존재하지 않는 도서입니다.")));
    }

    @Override
    public BookDto getBookByIsbn(String isbn) {
        return bookMapper.toDto(bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NoSuchBookException("존재하지 않는 도서입니다.")));
    }

    @Override
    public CursorPageResponseBookDto getBooksByPage(PagingBookRequest pagingBookRequest) {
        return null;
    }

    @Override
    public BookDto createBook(BookCreateRequest bookCreateRequest, @Nullable MultipartFile file) {

        if (bookRepository.existsByIsbn(bookCreateRequest.isbn())) {
            throw new AllReadyExistsIsbnException("이미 존재하는 ISBN 입니다.");
        }



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
