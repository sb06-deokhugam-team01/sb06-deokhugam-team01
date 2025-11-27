package com.sprint.sb06deokhugamteam01.dto.User.response;

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



}
