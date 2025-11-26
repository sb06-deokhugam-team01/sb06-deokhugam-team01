package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CursorPageReviewRequest(
        UUID userId, // 작성자 ID
        UUID bookId,
        String keyword,
        SortOrder orderBy,
        SortDirection direction,
        String cursor,
        LocalDateTime after,
        int limit,
        UUID requestUserId // 요청자 ID
) {

    // Swagger 이름에 따라 카멜케이스로 작성
    public enum SortOrder {
        createdAt,
        rating
    }

    public enum SortDirection {
        ASC,
        DESC
    }

}
