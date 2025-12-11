package com.sprint.sb06deokhugamteam01.dto.review.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewCreateRequest (

        @NotNull(message = "도서 ID는 공백일 수 없습니다.")
        UUID bookId,

        @NotNull(message = "사용자 ID는 공백일 수 없습니다.")
        UUID userId,

        @NotBlank(message = "리뷰 내용은 비어있을 수 없습니다.")
        String content,

        @NotNull(message = "평점은 비어있을 수 없습니다.")
        @Min(value = 1, message = "평점은 1과 5 사이의 정수여야 합니다.")
        @Max(value = 5, message = "평점은 1과 5 사이의 정수여야 합니다.")
        int rating

){
}
