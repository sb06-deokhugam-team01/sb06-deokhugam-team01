package com.sprint.sb06deokhugamteam01.service.user;

import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.User.request.PowerUserRequest;
import com.sprint.sb06deokhugamteam01.dto.User.request.UserRegisterRequest;
import com.sprint.sb06deokhugamteam01.dto.User.request.UserUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.User.response.CursorPageResponsePowerUserDto;
import com.sprint.sb06deokhugamteam01.dto.User.response.UserDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.cglib.core.Local;

public interface UserService {

    User createUser(UserRegisterRequest request);

    User login(String email, String password);

    User getUser(UUID userId);

    User deactivateUser(UUID userId, UUID currentUserId);

    User updateUser(UUID userId, String request, UUID currentUserId);

    User deleteUser(UUID userId, UUID currentUserId);

    void hardDeleteUser(UUID userId);

    CursorPageResponsePowerUserDto getPowerUserList(PowerUserRequest request);
}
