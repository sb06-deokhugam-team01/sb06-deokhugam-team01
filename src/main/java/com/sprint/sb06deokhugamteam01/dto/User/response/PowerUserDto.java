package com.sprint.sb06deokhugamteam01.dto.User.response;

import com.sprint.sb06deokhugamteam01.domain.batch.BatchUserRating;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PowerUserDto(
    UUID userId,
    String nickname,
    String period,
    LocalDateTime createdAt,
    int rank,
    double score,
    double reviewScoreSum,
    int likeCount,
    int commentCount
) {
    public static PowerUserDto fromBatchUserRating(BatchUserRating rating) {
        return PowerUserDto.builder()
            .userId(rating.getUser().getId())
            .nickname(rating.getUser().getNickname())
            .period(rating.getPeriodType().name())
            .createdAt(rating.getCreatedAt())
            .rank(rating.getRank() != null ? rating.getRank() : 0)
            .score(rating.getScore())
            .reviewScoreSum(rating.getReviewPopularitySum())
            .likeCount(rating.getLikesMade())
            .commentCount(rating.getCommentsMade())
            .build();
    }
}
