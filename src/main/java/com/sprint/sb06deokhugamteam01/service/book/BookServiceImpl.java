package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.BookUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import com.sprint.sb06deokhugamteam01.dto.book.response.CursorPageResponseBookDto;
import com.sprint.sb06deokhugamteam01.exception.book.AlReadyExistsIsbnException;
import com.sprint.sb06deokhugamteam01.exception.book.NoSuchBookException;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements  BookService {

    private final BookRepository bookRepository;

    @Override
    public BookDto getBookById(UUID id) {
        return BookDto.fromEntity(bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchBookException(detailMap("id", id))));
    }

    @Override
    public BookDto getBookByIsbn(String isbn) {
        return BookDto.fromEntity(bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NoSuchBookException(detailMap("isbn", isbn))));
    }

    @Override
    public CursorPageResponseBookDto getBooksByPage(PagingBookRequest pagingBookRequest) {
        return null;
    }

    @Transactional
    @Override
    public BookDto createBook(BookCreateRequest bookCreateRequest, @Nullable MultipartFile file) {

        if (bookRepository.existsByIsbn(bookCreateRequest.isbn())) {
            throw new AlReadyExistsIsbnException(detailMap("isbn", bookCreateRequest.isbn()));
        }

        Book book = BookCreateRequest.fromDto(bookCreateRequest);

        //ToDo: S3 파일 업로드 처리

        return BookDto.fromEntity(bookRepository.save(book));

    }

    @Transactional
    @Override
    public BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest, @Nullable MultipartFile file) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchBookException(detailMap("id", id)));

        book.updateBook(
                bookUpdateRequest.title(),
                bookUpdateRequest.author(),
                bookUpdateRequest.description(),
                bookUpdateRequest.publisher(),
                bookUpdateRequest.publishedDate()
        );

        return BookDto.fromEntity(book);

    }

    @Transactional
    @Override
    public void deleteBookById(UUID id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchBookException(detailMap("id", id)));

        book.softDelete();

    }

    @Transactional
    @Override
    public void hardDeleteBookById(UUID id) {

        if (!bookRepository.existsById(id)) {
            throw new NoSuchBookException(detailMap("id", id));
        }

        bookRepository.deleteById(id);

        //ToDo: 연관관계 매핑된 리뷰들 모두 삭제하기

    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }

}
