package com.sprint.sb06deokhugamteam01.domain.book;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookOrderBy {
    TITLE("title"),
    PUBLISHED_DATE("publishedDate"),
    RATING("rating"),
    REVIEW_COUNT("reviewCount");

    private final String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public static BookOrderBy withFieldName(String fieldName) {

        for (BookOrderBy orderBy : BookOrderBy.values()) {
            if (orderBy.getFieldName().equals(fieldName)) {
                return orderBy;
            }
        }
        throw new IllegalArgumentException("Invalid field name: " + fieldName);
    }

}
