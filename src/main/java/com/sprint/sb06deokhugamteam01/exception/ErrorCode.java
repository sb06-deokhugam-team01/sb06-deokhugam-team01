package com.sprint.sb06deokhugamteam01.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("User not found", 404, "U001"),
    INVALID_USER("Invalid user", 400, "U002"),
    REVIEW_NOT_FOUND("Review not found", 404, "R001"),
    COMMENT_NOT_FOUND("Comment not found", 404, "C001"),
    BOOK_NOT_FOUND("Book not found", 404, "B001"),
    INVALID_REQUEST("Invalid request", 400, "COM001"),
    INTERNAL_SERVER_ERROR("Internal server error", 500, "COM002");

    private final String message;
    private final int status;
    private final String code;

}
