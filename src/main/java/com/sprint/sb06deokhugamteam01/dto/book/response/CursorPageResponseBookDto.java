package com.sprint.sb06deokhugamteam01.dto.book.response;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CursorPageResponseBookDto {

    private List<BookDto> content;
    private String nextCursor;
    private String nextAfter;
    private int size;
    private int totalElements;
    private boolean hasNext;

}
