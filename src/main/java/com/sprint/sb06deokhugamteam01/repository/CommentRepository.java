package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.domain.Comment;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.repository.comment.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM comments WHERE review_id IN (:#{#reviews.![id]})", nativeQuery = true)
    void deleteByReviewIn(@Param("reviews") List<Review> reviewList);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM comments WHERE review_id = :#{#review.id}", nativeQuery = true)
    void deleteAllByReview(@Param("review") Review review);

    // findById (논리 삭제 처리된 댓글을 포함하여 조회)
    @Query(value = "SELECT * FROM comments WHERE id=:id", nativeQuery = true)
    Optional<Comment> findByIdAndIsActiveFalse(@Param("id") UUID commentId);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM comments WHERE id=:id", nativeQuery = true)
    void hardDeleteById(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.isActive = false")
    void deleteAllByIsActiveFalse();
}
