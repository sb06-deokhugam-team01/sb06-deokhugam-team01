package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CursorPageResponsePopularReviewDto (
        List<Object> content,
        String nextCursor,
        LocalDateTime nextAfter,
        int size,
        long totalElements,
        boolean hasNext
){

}
