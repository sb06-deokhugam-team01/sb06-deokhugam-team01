package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.domain.Comment;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    void deleteByReviewIn(List<Review> reviewList);

}
