package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.domain.Comment;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.repository.comment.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {

    void deleteByReviewIn(List<Review> reviewList);

    void deleteAllByReview(Review review);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.isActive = false")
    void deleteAllByIsActiveFalse();
}
