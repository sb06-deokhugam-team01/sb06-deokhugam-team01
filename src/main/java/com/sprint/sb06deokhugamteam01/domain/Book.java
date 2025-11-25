package com.sprint.sb06deokhugamteam01.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
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

}
