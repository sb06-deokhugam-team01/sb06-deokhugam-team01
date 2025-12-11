package com.sprint.sb06deokhugamteam01.repository.batch;

import com.sprint.sb06deokhugamteam01.domain.batch.BatchReviewRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchReviewRatingRepository extends JpaRepository<BatchReviewRating, UUID> {

    Optional<BatchReviewRating> findByPeriodTypeAndPeriodStartAndPeriodEndAndReview_Id(
            PeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            UUID reviewId
    );

    void deleteByReview_Id(UUID reviewId);

    void deleteByReview_IdIn(Iterable<UUID> reviewIds);

}
