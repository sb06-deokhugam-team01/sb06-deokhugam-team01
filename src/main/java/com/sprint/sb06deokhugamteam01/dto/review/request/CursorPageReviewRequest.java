package com.sprint.sb06deokhugamteam01.dto.review.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CursorPageReviewRequest(

        @Nullable
        UUID userId, // 작성자 ID

        @Nullable
        UUID bookId,

        @Nullable
        String keyword, // 작성자 닉네임 | 내용

        @Nullable
        SortField orderBy,

        @Nullable
        SortDirection direction,

        @Nullable
        String cursor,

        @Nullable
        LocalDateTime after,

        @Nullable
        @Min(value = 1)
        Integer limit
) {

    // Swagger 이름에 따라 카멜케이스로 작성
    public enum SortField {
        createdAt,
        rating
    }

    public enum SortDirection {
        ASC,
        DESC
    }

}
