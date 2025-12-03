package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.domain.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.review.ReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.review.*;
import com.sprint.sb06deokhugamteam01.exception.book.NoSuchBookException;
import com.sprint.sb06deokhugamteam01.exception.review.ReviewAlreadyExistsException;
import com.sprint.sb06deokhugamteam01.exception.review.ReviewNotFoundException;
import com.sprint.sb06deokhugamteam01.exception.user.InvalidUserException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ReviewDto createReview(ReviewCreateRequest request) {

        UUID userId = request.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", userId)));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new NoSuchBookException(detailMap("bookId", request.bookId())));

        // 누가 어떤 책에 대해 이미 리뷰를 쓴건지 정보를 담음
        if (reviewRepository.existsByUserAndBook(user, book)) {
            Map<String, Object> details = new HashMap<>();
            details.put("userId", user.getId());
            details.put("bookId", book.getId());
            throw new ReviewAlreadyExistsException(details);
        }

        Review review = Review.builder()
                .rating(request.rating())
                .content(request.content())
                .likeCount(0)
                .commentCount(0)
                .isActive(true)
                .user(user)
                .book(book)
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewDto.from(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDto getReview(UUID reviewId, UUID requestUserId) {

        userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(detailMap("reviewId", reviewId)));

        return ReviewDto.from(review);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseReviewDto getReviews(CursorPageReviewRequest request, UUID requestUserId) {

        // 기본값 처리
        int limit = request.limit() != null
                ? request.limit()
                : 50;
        CursorPageReviewRequest.SortDirection sortDirection
                = request.direction() != null
                ? request.direction()
                : CursorPageReviewRequest.SortDirection.DESC;
        CursorPageReviewRequest.SortField sortField
                = request.orderBy() != null
                ? request.orderBy()
                : CursorPageReviewRequest.SortField.createdAt;

        boolean ascending = (sortDirection == CursorPageReviewRequest.SortDirection.ASC);
        boolean useRating = (sortField == CursorPageReviewRequest.SortField.rating);

        // 커서 페이징 설정
        Pageable pageable = PageRequest.of(0, limit);

        // 필터링 조건과 JPA메서드 연결
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .userId(request.userId())
                .bookId(request.bookId())
                .keyword(request.keyword())
                .useRating(useRating)
                .ascending(ascending)
                .cursor(request.cursor())
                .after(request.after())
                .limit(request.limit())
                .build();

        Slice<Review> slice
                = reviewRepository.getReviews(condition, pageable);

        // DTO로 변환
        List<ReviewDto> content = slice.getContent().stream()
                .map(ReviewDto::from) // TODO 조회하는 사용자가 좋아요 눌렀는지 계산 필요
                .toList();

        // 커서 페이징 후처리
        boolean hasNext = slice.hasNext();
        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (!content.isEmpty() && hasNext) {
            Review lastReview = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = lastReview.getId().toString();
            nextAfter = lastReview.getCreatedAt();
        }

        return CursorPageResponseReviewDto.builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(content.size())
                .totalElements(content.size()) // slice로는 전체 개수를 알 수 없음. 현재 슬라이스 요소 개수 반환.
                .hasNext(hasNext)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponsePopularReviewDto getPopularReviews(CursorPagePopularReviewRequest request,
                                                                UUID requestUserId
    ) {
        // 기본값 처리
        int limit = request.limit() != null
                ? request.limit()
                : 50;
        CursorPagePopularReviewRequest.SortDirection sortDirection
                = request.direction() != null
                ? request.direction()
                : CursorPagePopularReviewRequest.SortDirection.ASC;
        CursorPagePopularReviewRequest.RankCriteria period
                = request.period() != null
                ? request.period()
                : CursorPagePopularReviewRequest.RankCriteria.DAILY;

        boolean descending = (sortDirection == CursorPagePopularReviewRequest.SortDirection.DESC);

        // 커서 페이징 설정
        Pageable pageable = PageRequest.of(0, limit);

        // 필터링 조건과 JPA메서드 연결
        PopularReviewSearchCondition condition = PopularReviewSearchCondition.builder()
                .period(period)
                .descending(descending)
                .cursor(request.cursor())
                .after(request.after())
                .limit(request.limit())
                .build();

        Slice<Review> slice = reviewRepository.getPopularReviews(condition, pageable);

        // DTO로 변환
        List<ReviewDto> content = slice.getContent().stream()
                .map(ReviewDto::from) // TODO 조회하는 사용자가 좋아요 눌렀는지 계산 필요
                .toList();

        // 커서 페이징 후처리
        boolean hasNext = slice.hasNext();
        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (!content.isEmpty() && hasNext) {
            Review lastReview = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = String.valueOf(lastReview.getLikeCount());
            nextAfter = lastReview.getCreatedAt();
        }

        return CursorPageResponsePopularReviewDto.builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(content.size())
                .totalElements(content.size())
                .hasNext(hasNext)
                .build();
    }

    @Override
    @Transactional
    public ReviewDto updateReview(UUID reviewId,
                                  ReviewUpdateRequest updateRequest,
                                  UUID requestUserId
    ) {
        User user = userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(detailMap("reviewId", reviewId)));

        // 작성자 == 요청자인지 검증
        if (!review.getUser().getId().equals(user.getId())) {
            throw new InvalidUserException(detailMap("userId", requestUserId));
        }

        if (updateRequest.content() != null) {
            review.updateContent(updateRequest.content());
        }
        if (updateRequest.rating() != null) {
            review.updateRating(updateRequest.rating());
        }

        Review savedReview = reviewRepository.save(review);
        return ReviewDto.from(savedReview);
    }

    @Override
    @Transactional
    public void deleteReview(UUID reviewId, UUID requestUserId) {

        userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(detailMap("reviewId", reviewId)));

        review.softDelete();
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void hardDeleteReview(UUID reviewId, UUID requestUserId) {

        userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(detailMap("reviewId", reviewId)));

        reviewRepository.delete(review);
        // 연관관계 매핑된 좋아요, 댓글, 활동점수 영구 삭제
        // TODO 좋아요를 DB에 저장해야 하지 않나?
        commentRepository.deleteAllByReview(review);
    }

    @Override
    @Transactional
    public ReviewLikeDto likeReview(UUID reviewId, UUID requestUserId) {

        userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(detailMap("reviewId", reviewId)));

        // TODO 유저-좋아요 정보 저장 필요
        review.increaseLikeCount();
        reviewRepository.save(review);

        return ReviewLikeDto.builder()
                .reviewId(review.getId())
                .userId(requestUserId)
                .liked(true)
                .build();

    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }
}
