package com.sprint.sb06deokhugamteam01.dto.review;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record ReviewUpdateRequest(

        @Size(min = 20, max = 1000, message = "리뷰 내용은 20자 이상 1000자 이하여야 합니다.")
        String content,

        @Min(value = 1, message = "평점은 1과 5 사이의 정수여야 합니다.")
        @Max(value = 5, message = "평점은 1과 5 사이의 정수여야 합니다.")
        Integer rating
) {
}
