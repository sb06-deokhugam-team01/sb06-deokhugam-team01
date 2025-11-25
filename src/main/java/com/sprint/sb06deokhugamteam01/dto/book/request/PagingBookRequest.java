package com.sprint.sb06deokhugamteam01.dto.book.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PagingBookRequest(
     String keyword,
     String orderBy,
     String direction,
     String cursor,
     LocalDateTime after,
     Integer limit
) {
}
