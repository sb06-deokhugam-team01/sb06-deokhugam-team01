package com.sprint.sb06deokhugamteam01.domain.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String publisher;

    private LocalDate publishedDate;

    private String isbn;

    private String thumbnailUrl;

    private int reviewCount = 0;

    private double rating = 0.0;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private boolean isActive = true;

    @Builder
    public Book(String title, String author, String description, String publisher, LocalDate publishedDate, String isbn) {

        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbn = isbn;

    }

    public void updateBook(String title, String author, String description, String publisher, LocalDate publishedDate) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
    }

    public void updateThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateRating(double rating) {
        this.rating = rating;
    }

    public void addNewReview(double score) {

        double totalScore = this.rating * this.reviewCount;
        totalScore += score;
        this.reviewCount += 1;
        this.rating = totalScore / this.reviewCount;

    }

    public void removeReview(double score) {

        if (this.reviewCount <= 1) {
            this.reviewCount = 0;
            this.rating = 0.0;
            return;
        }

        double totalScore = this.rating * this.reviewCount;
        totalScore -= score;
        this.reviewCount -= 1;
        this.rating = totalScore / this.reviewCount;

    }

    public void softDelete() {
        this.isActive = false;
    }

}
