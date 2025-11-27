package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;

import java.util.UUID;

/**
 * Swagger에 명시되지 않았지만 필요하다고 생각해 추가했습니다.
 * 리뷰 단일조회, 수정, 삭제(soft, hard), 좋아요 메서드의 인자로 사용됩니다.
 */
@Builder
public record ReviewOperationRequest(
        UUID reviewId
) {

}
