package com.sprint.sb06deokhugamteam01.repository.user;

import com.sprint.sb06deokhugamteam01.domain.batch.BatchUserRating;
import com.sprint.sb06deokhugamteam01.dto.User.request.PowerUserRequest;
import java.time.LocalDateTime;
import org.springframework.data.domain.Slice;

public interface CustomBatchUserRatingRepository {

    Slice<BatchUserRating> getBatchUserRatingList(PowerUserRequest request);

}
