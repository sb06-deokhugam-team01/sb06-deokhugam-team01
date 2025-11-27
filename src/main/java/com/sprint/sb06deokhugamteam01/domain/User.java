package com.sprint.sb06deokhugamteam01.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String nickname;
    private String password;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private boolean isActive;
    private LocalDateTime deletedAt;

    public void activate() {
        this.isActive = true;
        this.deletedAt = null;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateProfile(String nickname) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
    }

    public void markDeleted(LocalDateTime now) {
        this.isActive = false;
        this.deletedAt = now;
    }

    public boolean isPendingDeletion(LocalDateTime threshold) {
        return deletedAt != null && deletedAt.isBefore(threshold);
    }
}
