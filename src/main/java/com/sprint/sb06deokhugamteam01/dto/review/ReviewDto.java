package com.sprint.sb06deokhugamteam01.dto.review;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReviewDto (
        UUID id,
        UUID bookId,
        String bookTitle,
        String bookThumbnailUrl,
        UUID userId,
        String userNickname,
        String content,
        int rating,
        int likeCount,
        int commentCount,
        boolean likedByMe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
    public static ReviewDto from(Review review){
        User user = review.getUser();
        Book book = review.getBook();

        return ReviewDto.builder()
                .id(review.getId())

                .bookId(book != null ? book.getId() : null)
                .bookTitle(book != null ? book.getTitle() : null)
                .bookThumbnailUrl(book != null ? book.getThumbnailUrl() : null)

                .userId(user != null ? user.getId() : null)
                .userNickname(user != null ? user.getNickname() : null)

                .content(review.getContent()) // TODO N+1 문제 안생기게 조치 필요
                .rating(review.getRating()) // TODO N+1 문제 안생기게 조치 필요
                .likeCount(review.getLikeCount())
                .commentCount(review.getCommentCount())

                .likedByMe(false) // TODO 인자로 requestUserId 받아서 처리 필요
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
