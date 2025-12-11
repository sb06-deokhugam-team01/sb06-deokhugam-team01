package com.sprint.sb06deokhugamteam01.domain.batch;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "batch_book_rating",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_batch_book_period",
                columnNames = {"period_type", "period_start", "period_end", "book_id"}
        ),
        indexes = {
                @Index(name = "idx_batch_book_period_rank", columnList = "period_type, period_end, rank"),
                @Index(name = "idx_batch_book_period_book", columnList = "period_type, period_end, book_id")
        }
)
public class BatchBookRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false, length = 16)
    private PeriodType periodType;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "avg_rating", nullable = false)
    private double avgRating;

    @Column(nullable = false)
    private double score;

    private Integer rank;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public void applySnapshot(
            PeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            Book book,
            int reviewCount,
            double avgRating,
            double score,
            Integer rank
    ) {
        this.periodType = periodType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.book = book;
        this.reviewCount = reviewCount;
        this.avgRating = avgRating;
        this.score = score;
        this.rank = rank;
    }
}
