package com.sprint.sb06deokhugamteam01.dto.User.response;

import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponsePowerUserDto(
    List<PowerUserDto> content,
    String nextCursor,
    String nextAfter,
    int size,
    int totalElements,
    boolean hasNext
) {

}
