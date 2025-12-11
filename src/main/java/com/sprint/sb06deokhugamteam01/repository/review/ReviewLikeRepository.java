package com.sprint.sb06deokhugamteam01.repository.review;

import com.sprint.sb06deokhugamteam01.domain.ReviewLike;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {

    Optional<ReviewLike> findByUserAndReview(User user, Review review);

    boolean existsByUserAndReview(User user, Review review);

    @Query("SELECT r.review.id FROM ReviewLike r WHERE r.user.id = :userId AND r.review.id IN :reviewIds")
    List<UUID> findLikedReviewIdsByUserIdAndReviewIds(@Param("userId") UUID userId, @Param("reviewIds") List<UUID> reviewIds);

    void deleteByReview(Review review);
}
