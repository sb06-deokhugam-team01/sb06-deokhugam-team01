package com.sprint.sb06deokhugamteam01.mapper;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.dto.review.ReviewDto;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * DTO의 from()만으로는 리포지토리 접근이 어려우므로 Mapper로 분리
 */
@Component
@RequiredArgsConstructor
public class ReviewMapper {

    private final ReviewLikeRepository reviewLikeRepository;

    public ReviewDto toDto(Review review, User requestUser){
        User user = review.getUser();
        Book book = review.getBook();

        boolean liked = reviewLikeRepository.existsByUserAndReview(requestUser, review);

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

                .likedByMe(liked)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
