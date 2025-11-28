package com.sprint.sb06deokhugamteam01.repository.review;

import com.sprint.sb06deokhugamteam01.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

    List<Review> findByBook_Id(UUID bookId);

    void deleteByBook_Id(UUID bookId);

}
