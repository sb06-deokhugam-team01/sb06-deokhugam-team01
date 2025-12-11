package com.sprint.sb06deokhugamteam01.dto.book.response;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public record CursorPageResponseBookDto(
        List<BookDto> content,
        String nextCursor,
        String nextAfter,
        int size,
        int totalElements,
        boolean hasNext
) {

}
