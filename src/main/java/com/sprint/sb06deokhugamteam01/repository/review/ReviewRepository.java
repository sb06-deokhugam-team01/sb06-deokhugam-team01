package com.sprint.sb06deokhugamteam01.repository.review;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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

}
