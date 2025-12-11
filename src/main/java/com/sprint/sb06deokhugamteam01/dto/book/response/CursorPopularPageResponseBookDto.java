package com.sprint.sb06deokhugamteam01.dto.book.response;

import com.sprint.sb06deokhugamteam01.dto.book.PopularBookDto;
import lombok.Builder;

import java.util.List;

@Builder
public record CursorPopularPageResponseBookDto(
        List<PopularBookDto> content,
        String nextCursor,
        String nextAfter,
        int size,
        int totalElements,
        boolean hasNext
) {
}
