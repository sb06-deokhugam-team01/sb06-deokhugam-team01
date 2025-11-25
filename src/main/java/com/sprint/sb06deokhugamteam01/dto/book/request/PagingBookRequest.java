package com.sprint.sb06deokhugamteam01.dto.book.request;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
public record PagingBookRequest(
     String keyword,
     OrderBy orderBy,
     SortDirection sortDirection,
     String cursor,
     LocalDateTime after,
     Integer limit
) {

    @RequiredArgsConstructor
    public enum OrderBy {
        TITLE("title"),
        PUBLISHED_DATE("publishedDate"),
        RATING("rating"),
        REVIEW_COUNT("reviewCount");

        private final String fieldName;
    }

    public enum SortDirection {
        ASC,
        DESC
    }

}
