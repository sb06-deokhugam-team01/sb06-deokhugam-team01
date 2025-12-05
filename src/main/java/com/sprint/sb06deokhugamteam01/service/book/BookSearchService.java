package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;

public interface BookSearchService {

    BookDto searchBookByIsbn(String isbn);

}
