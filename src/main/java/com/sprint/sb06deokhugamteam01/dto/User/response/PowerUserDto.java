package com.sprint.sb06deokhugamteam01.dto.User.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PowerUserDto {

    private UUID userId;
    private String nickname;
    private String period;
    private LocalDateTime createdAt;
    private int rank;
    private double score;
    private double reviewScoreSum;
    private int likeCount;
    private int commentCount;

}
