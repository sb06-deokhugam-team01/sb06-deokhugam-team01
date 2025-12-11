package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.domain.ReviewLike;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.review.*;
import com.sprint.sb06deokhugamteam01.exception.book.BookNotFoundException;
import com.sprint.sb06deokhugamteam01.dto.review.ReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPagePopularReviewRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPageReviewRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.ReviewCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.ReviewUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.review.response.CursorPageResponsePopularReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.CursorPageResponseReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewLikeDto;
import com.sprint.sb06deokhugamteam01.exception.common.UnauthorizedAccessException;
import com.sprint.sb06deokhugamteam01.exception.review.InvalidReviewCursorException;
import com.sprint.sb06deokhugamteam01.exception.review.ReviewAlreadyExistsException;
import com.sprint.sb06deokhugamteam01.exception.review.ReviewNotFoundException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.mapper.ReviewMapper;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewLikeRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchReviewRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import com.sprint.sb06deokhugamteam01.service.book.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;
    private final BatchReviewRatingRepository batchReviewRatingRepository;
    private final NotificationRepository notificationRepository;
    private final ReviewMapper reviewMapper;
    private final S3StorageService s3StorageService;

    @Override
    @Transactional
    public ReviewDto createReview(ReviewCreateRequest request) {

        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", userId)));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException(detailMap("bookId", request.bookId())));

        if (reviewRepository.findByBookAndUserAndIsActiveTrue(book, user).isPresent()) {
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

        book.updateRatingOnNewReview(request.rating());
        bookRepository.save(book);

        String presignedUrl = s3StorageService.getPresignedUrl(book.getThumbnailUrl());
        return reviewMapper.toDto(savedReview, user, presignedUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDto getReview(UUID reviewId, UUID requestUserId) {

        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(detailMap("reviewId", reviewId)));

        Book book = review.getBook();
        String presignedUrl = s3StorageService.getPresignedUrl(book.getThumbnailUrl());
        return reviewMapper.toDto(review, requestUser, presignedUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseReviewDto getReviews(CursorPageReviewRequest request, UUID requestUserId) {

        userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

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

        // 커서 유효성 검증
        String cursor = request.cursor();
        if (cursor != null && !cursor.isEmpty()) {
            if (useRating) {
                try {
                    long longCursor = Long.parseLong(request.cursor());
                    if (longCursor < 1 || longCursor > 5) {
                        throw new InvalidReviewCursorException(detailMap("cursor", request.cursor()));
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidReviewCursorException(detailMap("cursor", request.cursor()));
                }
            } else {
                try {
                    LocalDateTime.parse(cursor);
                } catch (DateTimeParseException parseException){
                    throw new InvalidReviewCursorException(detailMap("cursor", cursor));
                }
            }
        }

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
                .limit(limit)
                .build();

        Slice<Review> slice
                = reviewRepository.getReviews(condition, pageable);

        // N+1 문제 해결 위해 ID를 추출해 사용
        List<UUID> reviewIds = slice.stream()
                .map(Review::getId)
                .toList();
        List<UUID> likedReviewIds = reviewLikeRepository.findLikedReviewIdsByUserIdAndReviewIds(requestUserId, reviewIds);

        // DTO로 변환
        List<ReviewDto> content = slice.getContent().stream()
                .map(review -> {
                    String presignedUrl = s3StorageService.getPresignedUrl(review.getBook().getThumbnailUrl());
                    return reviewMapper.toDto(review, likedReviewIds.contains(requestUserId), presignedUrl);
                })
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

        userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        // 커서 유효성 검증
        if (request.cursor() != null && !request.cursor().isEmpty()) {

            try {
                if (Long.parseLong(request.cursor()) < 0) {
                    throw new InvalidReviewCursorException(detailMap("cursor", request.cursor()));
                }
            } catch (NumberFormatException e) {
                throw new InvalidReviewCursorException(detailMap("cursor", request.cursor()));
            }
        }

        // 기본값 처리
        int limit = request.limit() != null
                ? request.limit()
                : 50;
        CursorPagePopularReviewRequest.SortDirection sortDirection
                = request.direction() != null
                ? request.direction()
                : CursorPagePopularReviewRequest.SortDirection.ASC;
        PeriodType period
                = request.period() != null
                ? request.period()
                : PeriodType.DAILY;

        boolean descending = (sortDirection == CursorPagePopularReviewRequest.SortDirection.DESC);

        // 커서 페이징 설정
        Pageable pageable = PageRequest.of(0, limit);

        // 필터링 조건과 JPA메서드 연결
        PopularReviewSearchCondition condition = PopularReviewSearchCondition.builder()
                .period(period)
                .descending(descending)
                .cursor(request.cursor())
                .after(request.after())
                .limit(limit)
                .build();

        Slice<Review> slice = reviewRepository.getPopularReviews(condition, pageable);

        // N+1 문제 해결 위해 ID를 추출해 사용
        List<UUID> reviewIds = slice.stream()
                .map(Review::getId)
                .toList();
        List<UUID> likedReviewIds = reviewLikeRepository.findLikedReviewIdsByUserIdAndReviewIds(requestUserId, reviewIds);

        // DTO로 변환
        List<ReviewDto> content = slice.getContent().stream()
                .map(review -> {
                    String presignedUrl = s3StorageService.getPresignedUrl(review.getBook().getThumbnailUrl());
                    return reviewMapper.toDto(review, likedReviewIds.contains(requestUserId), presignedUrl);
                })
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
            throw new UnauthorizedAccessException(detailMap("userId", requestUserId));
        }

        if (updateRequest.content() != null) {
            review.updateContent(updateRequest.content());
        }
        if (updateRequest.rating() != null) {
            int oldRating = review.getRating();
            int newRating = updateRequest.rating();
            review.updateRating(newRating);
            review.getBook().updateRatingOnReviewUpdate(oldRating, newRating);
            bookRepository.save(review.getBook());
        }

        Review savedReview = reviewRepository.save(review);
        String presignedUrl = s3StorageService.getPresignedUrl(review.getBook().getThumbnailUrl());
        return reviewMapper.toDto(savedReview, user, presignedUrl);
    }

    @Override
    @Transactional
    public void deleteReview(UUID reviewId, UUID requestUserId) {

        User user = userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(detailMap("reviewId", reviewId)));

        // 작성자 == 요청자인지 검증
        if (!review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException(detailMap("userId", requestUserId));
        }

        Book book = review.getBook();
        book.updateRatingOnReviewDelete(review.getRating());
        bookRepository.save(book);

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

        Book book = review.getBook();

        if (review.isActive()) {
            book.updateRatingOnReviewDelete(review.getRating());
        } else {
            book.updateRatingOnReviewHardDelete(review.getRating());
        }
        bookRepository.save(book);

        commentRepository.deleteAllByReview(review);
        reviewLikeRepository.deleteByReview(review);
        batchReviewRatingRepository.deleteByReview_Id(reviewId);
        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public ReviewLikeDto likeReviewToggle(UUID reviewId, UUID requestUserId) {

        User user = userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(detailMap("userId", requestUserId)));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(detailMap("reviewId", reviewId)));

        boolean liked;

        Optional<ReviewLike> existingReviewLike = reviewLikeRepository.findByUserAndReview(user, review);
        if (existingReviewLike.isPresent()) {
            reviewLikeRepository.delete(existingReviewLike.get());
            review.decreaseLikeCount();
            liked = false;
        }
        else {
            ReviewLike reviewLike = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .build();
            reviewLikeRepository.save(reviewLike);

            if (requestUserId != review.getUser().getId()) {
                Notification notification = Notification.builder()
                        .user(review.getUser())
                        .review(review)
                        .confirmed(false)
                        .content("[" + user.getNickname() + "]님이 나의 리뷰를 좋아합니다.")
                        .build();
                notificationRepository.save(notification);
            }

            review.increaseLikeCount();
            liked = true;
        }

        reviewRepository.save(review);

        return ReviewLikeDto.builder()
                .reviewId(review.getId())
                .userId(requestUserId)
                .liked(liked)
                .build();
    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }
}
