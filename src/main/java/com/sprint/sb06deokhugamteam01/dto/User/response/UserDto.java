package com.sprint.sb06deokhugamteam01.dto.User.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public record UserDto(
    UUID id,
    String email,
    String nickname,
    LocalDateTime createdAt
) {


}
