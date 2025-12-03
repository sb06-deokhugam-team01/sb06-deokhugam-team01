package com.sprint.sb06deokhugamteam01.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
        @NotBlank(message = "메시지 내용은 필수입니다.")
        @Size(max = 500, message = "메시지 내용은 500자 이하여야 합니다.")
        String content
) {
}
