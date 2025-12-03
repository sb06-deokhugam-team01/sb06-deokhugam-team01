package com.sprint.sb06deokhugamteam01.dto.book.naver;

import java.time.LocalDate;

public record BookData(
        String title,
        String link,
        String image,
        String author,
        String discount,
        String publisher,
        String pubdate,
        String isbn,
        String description
) {
    public LocalDate getPublishedDate() {
        if (pubdate.length() != 8) {
            return null;
        }
        return LocalDate.of(
                Integer.parseInt(pubdate.substring(0, 4)),  // year
                Integer.parseInt(pubdate.substring(4, 6)),  // month
                Integer.parseInt(pubdate.substring(6, 8))   // day
        );
    }
}
