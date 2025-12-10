package com.sprint.sb06deokhugamteam01.dto.review.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record ReviewUpdateRequest(

        @Nullable
        @Size(min = 1, message = "리뷰 내용을 입력해 주세요.")
        String content,

        @Nullable
        @Min(value = 1, message = "평점은 1과 5 사이의 정수여야 합니다.")
        @Max(value = 5, message = "평점은 1과 5 사이의 정수여야 합니다.")
        Integer rating
) {
}
