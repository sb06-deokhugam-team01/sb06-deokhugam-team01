package com.sprint.sb06deokhugamteam01.dto.User.response;

import com.sprint.sb06deokhugamteam01.domain.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String nickname,
    LocalDateTime createdAt
) {

    public static UserDto from(User user){
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getCreatedAt()
        );
    }

}
