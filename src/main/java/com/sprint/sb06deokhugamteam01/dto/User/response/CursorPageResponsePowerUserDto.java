package com.sprint.sb06deokhugamteam01.dto.User.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CursorPageResponsePowerUserDto {

    private List<PowerUserDto> content;
    private String nextCursor;
    private String nextAfter;
    private int size;
    private int totalElements;
    private boolean hasNext;
}
