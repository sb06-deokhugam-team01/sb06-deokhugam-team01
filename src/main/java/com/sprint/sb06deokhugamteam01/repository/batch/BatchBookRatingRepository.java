package com.sprint.sb06deokhugamteam01.repository.batch;

import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BatchBookRatingRepository extends JpaRepository<BatchBookRating, UUID> {

    Optional<BatchBookRating> findByPeriodTypeAndPeriodStartAndPeriodEndAndBook_Id(
            PeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            UUID bookId
    );

    @Modifying
    @Query("DELETE FROM BatchBookRating b WHERE b.book.id = :bookId")
    void deleteByBook_Id(UUID bookId);
}
