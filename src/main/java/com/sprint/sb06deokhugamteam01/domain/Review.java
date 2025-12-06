package com.sprint.sb06deokhugamteam01.domain;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
        @Index(name = "idx_createdAt_cursor", columnList = "createdAt, id"), // createdAt 기준 커서
        @Index(name = "idx_rating_cursor", columnList = "rating, createdAt, id") // rating 기준 커서
})
public class Review {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int rating;

//    @Lob
    @Column(length = 4000)
    private String content;

    private int likeCount;

    private int commentCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateRating(int rating) {
        this.rating = rating;
    }

    public void softDelete() {
        this.isActive = false;
    }

    public void restore(){
        this.isActive = true;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) this.commentCount--;
    }
}
