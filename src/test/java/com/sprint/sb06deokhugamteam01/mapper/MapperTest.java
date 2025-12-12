package com.sprint.sb06deokhugamteam01.mapper;

import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewDto;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MapperTest {
    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @InjectMocks
    private ReviewMapper reviewMapper;

    @Test
    @DisplayName("Review 엔티티 -> DTO 변환 테스트 (Review, User)")
    void toDto_Single() {
        // given
        User user = User.builder().id(UUID.randomUUID()).nickname("유저").build();
        Review review = Review.builder().id(UUID.randomUUID())
                .content("좋은 책입니다. 추천합니다.").user(user).build();
        String imageUrl = "url";
        given(reviewLikeRepository.existsByUserAndReview(any(), any())).willReturn(true);

        // when
        ReviewDto dto = reviewMapper.toDto(review, user, imageUrl);

        // then
        assertThat(dto.content()).isEqualTo(review.getContent());
        assertThat(dto.likedByMe()).isTrue();
    }

    @Test
    @DisplayName("Review 엔티티 -> DTO 변환 테스트 (Review, boolean)")
    void toDto_Bulk() {
        // given
        Review review = Review.builder().id(UUID.randomUUID())
                .content("좋은 책입니다. 추천합니다.").build();
        String imageUrl = "url";
        boolean isLiked = false;

        // when
        ReviewDto dto = reviewMapper.toDto(review, isLiked, imageUrl);

        // then
        assertThat(dto.content()).isEqualTo(review.getContent());
        assertThat(dto.likedByMe()).isFalse();
    }
}
