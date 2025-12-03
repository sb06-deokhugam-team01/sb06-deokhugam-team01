package com.sprint.sb06deokhugamteam01.controller;

import com.sprint.sb06deokhugamteam01.dto.review.*;
import com.sprint.sb06deokhugamteam01.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody @Valid ReviewCreateRequest request) {
        log.info("Request to create review: {}", request);
        ReviewDto response = reviewService.createReview(request);
        log.info("Review created: {}", response);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(
            @PathVariable UUID reviewId,
            @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
    ) {
        log.info("Request to get single review: {}", reviewId);
        ReviewDto response = reviewService.getReview(reviewId, requestUserId);
        log.info("Review get: {}", response);
        return ResponseEntity
                .ok(response);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseReviewDto> getReviews(
            @ModelAttribute @Valid CursorPageReviewRequest request,
            @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
    ) {
        log.info("Request to get reviews: {}", requestUserId);
        CursorPageResponseReviewDto response = reviewService.getReviews(request, requestUserId);
        log.info("Reviews: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<CursorPageResponsePopularReviewDto> getPopularReviews(
            @ModelAttribute @Valid CursorPagePopularReviewRequest request,
            @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
    ) {
        log.info("Request to get popular reviews: {}", requestUserId);
        CursorPageResponsePopularReviewDto response = reviewService.getPopularReviews(request, requestUserId);
        log.info("Popular reviews: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable UUID reviewId,
            @RequestBody @Valid ReviewUpdateRequest updateRequest,
            @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
    ) {
        log.info("Request to update review: {}", reviewId);
        ReviewDto response = reviewService.updateReview(reviewId, updateRequest, requestUserId);
        log.info("Review updated: {}", response);
        return ResponseEntity
                .ok(response);
    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ReviewLikeDto> likeReviewToggle(
            @PathVariable UUID reviewId,
            @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
    ) {
        log.info("Request to like/cancel review: {}", reviewId);
        ReviewLikeDto response = reviewService.likeReviewToggle(reviewId, requestUserId);
        log.info("Liked/Canceled review: {}", response);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {
        log.info("Request to delete review: {}", reviewId);
        reviewService.deleteReview(reviewId, requestUserId);
        log.info("Review deleted: {}", reviewId);
        return ResponseEntity
                .noContent().build();
    }

    @DeleteMapping("/{reviewId}/hard")
    public ResponseEntity<Void> hardDeleteReview(
            @PathVariable UUID reviewId,
            @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
    ) {
        log.info("Request to hard delete review: {}", reviewId);
        reviewService.hardDeleteReview(reviewId, requestUserId);
        log.info("Review hard deleted: {}", reviewId);
        return ResponseEntity
                .noContent().build();
    }

}
