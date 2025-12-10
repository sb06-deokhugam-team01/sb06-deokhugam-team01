package com.sprint.sb06deokhugamteam01.mapper;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewDto;
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

    // 단건 변환 시 사용
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

                .content(review.getContent())
                .rating(review.getRating())
                .likeCount(review.getLikeCount())
                .commentCount(review.getCommentCount())

                .likedByMe(liked)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // 다건 변환 시 사용
    public ReviewDto toDto(Review review, boolean liked){
        User user = review.getUser();
        Book book = review.getBook();

        return ReviewDto.builder()
                .id(review.getId())

                .bookId(book != null ? book.getId() : null)
                .bookTitle(book != null ? book.getTitle() : null)
                .bookThumbnailUrl(book != null ? book.getThumbnailUrl() : null)

                .userId(user != null ? user.getId() : null)
                .userNickname(user != null ? user.getNickname() : null)

                .content(review.getContent())
                .rating(review.getRating())
                .likeCount(review.getLikeCount())
                .commentCount(review.getCommentCount())

                .likedByMe(liked)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
