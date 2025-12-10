package com.sprint.sb06deokhugamteam01.repository.batch;

import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchBookRatingRepository extends JpaRepository<BatchBookRating, UUID> {

    Optional<BatchBookRating> findByPeriodTypeAndPeriodStartAndPeriodEndAndBook_Id(
            PeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            UUID bookId
    );

    void deleteByBook_Id(UUID bookId);
}
