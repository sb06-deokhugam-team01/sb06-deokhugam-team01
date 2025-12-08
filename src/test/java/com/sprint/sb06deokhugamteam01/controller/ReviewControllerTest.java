package com.sprint.sb06deokhugamteam01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPagePopularReviewRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPageReviewRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.ReviewCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.ReviewUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.review.response.CursorPageResponsePopularReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.CursorPageResponseReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewLikeDto;
import com.sprint.sb06deokhugamteam01.service.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BDDMockito 양식으로 작성
 */
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    private final UUID userId = UUID.randomUUID();
    private final UUID bookId = UUID.randomUUID();
    private final UUID bookId2 = UUID.randomUUID();
    private final UUID reviewId = UUID.randomUUID();
    private final UUID requestUserId = UUID.randomUUID();

    ReviewDto response;
    ReviewDto response2;

    @BeforeEach
    void setUp() {
        response = ReviewDto.builder()
                .id(reviewId)
                .bookId(bookId)
                .userId(userId)
                .content("응답DTO1 응답DTO1 응답DTO1 응답DTO1")
                .build();

        response2 = ReviewDto.builder()
                .id(reviewId)
                .bookId(bookId2)
                .userId(userId)
                .content("응답DTO2 응답DTO2 응답DTO2 응답DTO2")
                .build();
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() throws Exception {

        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookId(bookId)
                .userId(userId)
                .content("생성 테스트 내용 - 정상 요청")
                .rating(5)
                .build();

        ReviewDto response = ReviewDto.builder()
                .id(reviewId)
                .bookId(request.bookId())
                .userId(request.userId())
                .content(request.content())
                .build();

        given(reviewService.createReview(any(ReviewCreateRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Body 변환
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content").value(request.content()));
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 본문 공백문자")
    void createReview_failure() throws Exception {

        // given
        ReviewCreateRequest invalidRequest = ReviewCreateRequest.builder()
                .bookId(bookId)
                .userId(userId)
                .content("")
                .rating(5)
                .build();

        // when & then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 단건 조회 성공")
    void getReview_success() throws Exception {

        // given
        given(reviewService.getReview(eq(reviewId), eq(requestUserId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", requestUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()));
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공")
    void getReviews_success() throws Exception {

        // given
        CursorPageReviewRequest request = CursorPageReviewRequest.builder()
                .userId(userId)
                .bookId(bookId)
                .keyword(null)
                .limit(10)
                .build();

        List<ReviewDto> content = List.of(response, response2);
        CursorPageResponseReviewDto response = CursorPageResponseReviewDto.builder()
                .content(content)
                .nextCursor(null)
                .nextAfter(null)
                .size(content.size())
                .totalElements(content.size())
                .hasNext(false)
                .build();

        given(reviewService.getReviews(any(CursorPageReviewRequest.class), eq(requestUserId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/reviews")
                        .param("userId", request.userId().toString())
                        .param("bookId", request.bookId().toString())
                        .param("limit", request.limit().toString())
                        .header("Deokhugam-Request-User-ID", requestUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(reviewService).getReviews(any(CursorPageReviewRequest.class), eq(requestUserId));
    }

    @Test
    @DisplayName("인기 리뷰 조회 성공")
    void getPopularReviews_success() throws Exception {

        // given
        CursorPagePopularReviewRequest request = CursorPagePopularReviewRequest.builder()
                .period(PeriodType.DAILY)
                .limit(10)
                .build();

        List<ReviewDto> content = List.of(response, response2);
        CursorPageResponsePopularReviewDto response = CursorPageResponsePopularReviewDto.builder()
                .content(content)
                .size(content.size())
                .totalElements(content.size())
                .hasNext(false)
                .build();

        given(reviewService.getPopularReviews(any(CursorPagePopularReviewRequest.class), eq(requestUserId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/reviews/popular")
                        .param("period", request.period().toString())
                        .param("limit", request.limit().toString())
                        .header("Deokhugam-Request-User-ID", requestUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(reviewService).getPopularReviews(any(CursorPagePopularReviewRequest.class), eq(requestUserId));
    }

    @Test
    @DisplayName("인기 리뷰 조회 실패 - 잘못된 limit 요청")
    void getPopularReviews_failure() throws Exception {

        // given
        CursorPagePopularReviewRequest request = CursorPagePopularReviewRequest.builder()
                .period(PeriodType.DAILY)
                .limit(-1)
                .build();

        // when & then
        mockMvc.perform(get("/api/reviews/popular")
                        .param("period", request.period().toString())
                        .param("limit", request.limit().toString())
                        .header("Deokhugam-Request-User-ID", requestUserId))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).getPopularReviews(any(), any());
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_success() throws Exception {

        // given
        ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder()
                .content("수정된내용 수정된내용 수정된내용 수정된내용 ")
                .rating(4)
                .build();

        ReviewDto response = ReviewDto.builder()
                .id(reviewId)
                .bookId(bookId2)
                .userId(userId)
                .content(updateRequest.content())
                .rating(updateRequest.rating())
                .build();

        given(reviewService.updateReview(eq(reviewId), any(ReviewUpdateRequest.class), eq(requestUserId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", requestUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.content").value(updateRequest.content()))
                .andExpect(jsonPath("$.rating").value(updateRequest.rating()));

        verify(reviewService).updateReview(eq(reviewId), any(ReviewUpdateRequest.class), eq(requestUserId));
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 평점 범위 초과")
    void updateReview_fail() throws Exception {

        // given
        ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder()
                .rating(6)
                .build();

        // when & then
        mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", requestUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).updateReview(any(), any(), any());
    }

    @Test
    @DisplayName("리뷰 좋아요 성공")
    void likeReview_success() throws Exception {

        // given
        ReviewLikeDto response = ReviewLikeDto.builder()
                .reviewId(reviewId)
                .userId(requestUserId)
                .liked(true)
                .build();

        given(reviewService.likeReviewToggle(eq(reviewId), eq(requestUserId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Deokhugam-Request-User-ID", requestUserId))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(reviewId.toString()))
                .andExpect(jsonPath("$.liked").value(true));

        verify(reviewService).likeReviewToggle(reviewId, requestUserId);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_success() throws Exception {

        // given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", userId))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(reviewId, userId);
    }

    @Test
    @DisplayName("리뷰 물리 삭제 성공")
    void hardDeleteReview_success() throws Exception {

        // given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
                        .header("Deokhugam-Request-User-ID", userId))
                .andExpect(status().isNoContent());

        verify(reviewService).hardDeleteReview(reviewId, userId);
    }
}
