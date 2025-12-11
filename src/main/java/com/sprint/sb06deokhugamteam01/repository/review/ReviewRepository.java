package com.sprint.sb06deokhugamteam01.repository.review;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

    boolean existsByUserAndBook(User user, Book book);

    List<Review> findByBook_Id(UUID bookId);

    void deleteByBook_Id(UUID bookId);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.isActive = false")
    void deleteAllByIsActiveFalse();

    Optional<Review> findByBookAndUserAndIsActiveTrue(Book book, User user);

    Optional<Review> findByIdAndIsActiveTrue(UUID id);
}
