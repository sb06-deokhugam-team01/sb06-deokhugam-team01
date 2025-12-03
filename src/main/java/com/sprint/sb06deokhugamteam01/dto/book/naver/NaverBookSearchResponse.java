package com.sprint.sb06deokhugamteam01.dto.book.naver;

public record NaverBookSearchResponse(
        String lastBuildDate,
        int total,
        int start,
        int display,
        BookData[] items
) {
}
