package com.sprint.sb06deokhugamteam01.dto.review.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CursorPageResponsePopularReviewDto (
        List<ReviewDto> content,
        String nextCursor,
        LocalDateTime nextAfter,
        int size,
        long totalElements,
        boolean hasNext
){

}
