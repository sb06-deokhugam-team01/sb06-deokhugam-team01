package com.sprint.sb06deokhugamteam01.service.user;

import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.User.request.UserRegisterRequest;
import com.sprint.sb06deokhugamteam01.dto.User.response.UserDto;
import java.time.LocalDateTime;
import java.util.UUID;

public interface UserService {

    User createUser(UserRegisterRequest request);

    User login(String email, String password);

    User getUser(UUID userId);

    User deactivateUser(UUID userId);

    User updateUser(UUID userId, String nickname);

    User deleteUser(UUID userId);

    void hardDeleteUser(UUID userId);

    void purgeDeletedUsersBefore(LocalDateTime cutoff);
}
