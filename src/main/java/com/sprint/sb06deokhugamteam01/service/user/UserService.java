package com.sprint.sb06deokhugamteam01.service.user;

import com.sprint.sb06deokhugamteam01.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public interface UserService {

    User createUser(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 2, max = 20) String nickname,
        @NotBlank @Size(min = 8) String password
    );

    User login(
        @Email @NotBlank String email,
        @NotBlank String password
    );

    User getUser(UUID userId);

    User deactivateUser(UUID userId);

    User updateUser(UUID userId, @NotBlank @Size(min = 2, max = 20) String nickname);

    User deleteUser(UUID userId);

    void hardDeleteUser(UUID userId);

    void purgeDeletedUsersBefore(LocalDateTime cutoff);
}
