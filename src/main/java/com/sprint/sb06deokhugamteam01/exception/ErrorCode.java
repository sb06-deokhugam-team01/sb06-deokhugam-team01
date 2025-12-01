package com.sprint.sb06deokhugamteam01.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_EXISTS_ISBN("All ready exists ISBN", 400, "B002"),
    BOOK_INFO_FETCH_FAILED("Book info fetch failed", 500, "B003"),
    INVALID_ISBN("Invalid ISBN", 400, "B004"),
    // S3_UPLOAD_FAILED("S3 upload failed", 500, "COM003"),
    USER_NOT_FOUND("User not found", 404, "U001"),
    INVALID_USER("Invalid user", 400, "U002"),

    REVIEW_NOT_FOUND("Review not found", 404, "R001"),
    REVIEW_ALREADY_EXISTS("Review already exists", 409, "R002"),

    COMMENT_NOT_FOUND("존재하지 않는 댓글입니다.", 404, "C001"),
    COMMENT_ACCESS_DENIED("해당 댓글에 대한 권한이 없습니다.", 403, "C002"),

    BOOK_NOT_FOUND("Book not found", 404, "B001"),

    INVALID_REQUEST("Invalid request", 400, "COM001"),
    INTERNAL_SERVER_ERROR("Internal server error", 500, "COM002"),
    NOTIFICATION_NOT_FOUND("Notification not found", 404, "N001"),
    UNAUTHORIZED_ACCESS("Unauthorized access", 401, "COM003");

    private final String message;
    private final int status;
    private final String code;

}
