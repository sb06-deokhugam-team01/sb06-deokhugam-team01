package com.sprint.sb06deokhugamteam01.service.user;

import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.User.request.UserRegisterRequest;
import com.sprint.sb06deokhugamteam01.dto.User.response.UserDto;
import com.sprint.sb06deokhugamteam01.exception.common.UnauthorizedAccessException;
import com.sprint.sb06deokhugamteam01.exception.user.InvalidUserException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new InvalidUserException(detailMap("email", request.email()));
        }

        User user = User.builder()
            .id(UUID.randomUUID())
            .email(request.email())
            .nickname(request.nickname())
            .password(request.password())
            .createdAt(LocalDateTime.now())
            .isActive(true)
            .build();

        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(detailMap("email", email)));

        if (!user.isActive() || user.getDeletedAt() != null || !password.equals(user.getPassword())) {
            throw new InvalidUserException(detailMap("email", email));
        }

        return user;
    }

    @Override
    public User getUser(UUID userId) {
        return getActiveUser(userId);
    }

    @Override
    public User deactivateUser(UUID userId, UUID currentUserId) {
        User user = getActiveUser(userId);
        if(!userId.equals(currentUserId)) {
            throw new UnauthorizedAccessException(detailMap("userId", currentUserId));
        }
        user.deactivate();
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID userId, String nickname, UUID currentUserId) {
        User user = getActiveUser(userId);
        if(!userId.equals(currentUserId)) {
            throw new UnauthorizedAccessException(detailMap("userId", currentUserId));
        }
        user.updateProfile(nickname);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User deleteUser(UUID userId, UUID currentUserId) {
        User user = getExistingUser(userId);
        if(!userId.equals(currentUserId)) {
            throw new UnauthorizedAccessException(detailMap("userId", currentUserId));
        }
        user.markDeleted(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void hardDeleteUser(UUID userId) {
        User user = getExistingUser(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void purgeDeletedUsersBefore(LocalDateTime cutoff) {
        userRepository.deleteAllSoftDeletedBefore(cutoff);
    }

    private User getExistingUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(detailMap("userId", userId)));
    }

    private User getActiveUser(UUID userId) {
        User user = getExistingUser(userId);
        if (!user.isActive() || user.getDeletedAt() != null) {
            throw new InvalidUserException(detailMap("userId", userId));
        }
        return user;
    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }
}
