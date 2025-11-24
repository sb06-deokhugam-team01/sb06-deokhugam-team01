package com.sprint.sb06deokhugamteam01.service.user;

import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.exception.user.InvalidUserException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(String email, String nickname, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidUserException(detailMap("email", email));
        }

        User user = User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .nickname(nickname)
            .password(password)
            .createdAt(LocalDateTime.now())
            .isActive(true)
            .build();

        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(detailMap("email", email)));

        if (!user.isActive() || !user.getPassword().equals(password)) {
            throw new InvalidUserException(detailMap("email", email));
        }

        return user;
    }

    @Override
    public User getUser(UUID userId) {
        return getActiveUser(userId);
    }

    @Override
    public User deactivateUser(UUID userId) {
        User user = getActiveUser(userId);
        user.deactivate();
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID userId, String nickname) {
        User user = getActiveUser(userId);
        user.updateProfile(nickname);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = getExistingUser(userId);
        userRepository.delete(user);
    }

    private User getExistingUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(detailMap("userId", userId)));
    }

    private User getActiveUser(UUID userId) {
        User user = getExistingUser(userId);
        if (!user.isActive()) {
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
