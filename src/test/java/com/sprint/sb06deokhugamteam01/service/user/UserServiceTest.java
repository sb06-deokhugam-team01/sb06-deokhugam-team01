package com.sprint.sb06deokhugamteam01.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.User.request.UserRegisterRequest;
import com.sprint.sb06deokhugamteam01.exception.common.UnauthorizedAccessException;
import com.sprint.sb06deokhugamteam01.exception.user.InvalidUserException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl target;

    private Answer<User> returnFirstArgument;
    private EasyRandom easyRandom;

    @BeforeEach
    void setUp() {
        returnFirstArgument = invocation -> invocation.getArgument(0);
        EasyRandomParameters parameters = new EasyRandomParameters()
            .stringLengthRange(5, 15);
        easyRandom = new EasyRandom(parameters);
    }

    @Test
    void registerUser_shouldPersistNewActiveUser() {
        String email = "user@example.com";
        String password = "password123";
        String nickname = "tester";

        UserRegisterRequest request = new UserRegisterRequest(email, password, nickname);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            Field id = User.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(user, UUID.randomUUID());
            return user;
        });

        User result = target.createUser(request);

        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getId()).isNotNull();
        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_whenEmailAlreadyExists_shouldThrowInvalidUserException() {
        String email = "duplicate@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);
        UserRegisterRequest request = new UserRegisterRequest(email, "password123", "tester");

        assertThatThrownBy(() -> target.createUser(request))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void login_shouldReturnActiveUserWhenCredentialsMatch() {
        User user = randomUser(true);
        String email = user.getEmail();
        String password = user.getPassword();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = target.login(email, password);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void login_withWrongPassword_shouldThrowInvalidUserException() {
        User user = randomUser(true);
        String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> target.login(email, "wrong"))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void getUser_shouldReturnActiveUserById() {
        User user = randomUser(true);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = target.getUser(userId);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUser_whenUserInactive_shouldThrowInvalidUserException() {
        User user = randomUser(false);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> target.getUser(userId))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void deactivateUser_shouldMarkUserAsInactive() {
        User user = randomUser(true);
        UUID userId = user.getId();
        UUID currentUserId = userId;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        User result = target.deactivateUser(userId, currentUserId);

        assertThat(result.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_whenCurrentUserDiffers_shouldThrowUnauthorizedAccess() {
        User user = randomUser(true);
        UUID userId = user.getId();
        UUID otherUserId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> target.deactivateUser(userId, otherUserId))
            .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void updateUser_shouldUpdateNicknameOnly() {
        User user = randomUser(true);
        UUID userId = user.getId();
        String newNickname = "renamedUser";
        UUID currentUserId = userId;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        User result = target.updateUser(userId, newNickname, currentUserId);

        assertThat(result.getNickname()).isEqualTo(newNickname);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_whenCurrentUserDiffers_shouldThrowUnauthorizedAccess() {
        User user = randomUser(true);
        UUID userId = user.getId();
        UUID currentUserId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> target.updateUser(userId, "nick", currentUserId))
            .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void deleteUser_shouldRemoveUserFromRepository() {
        User user = randomUser(true);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        target.hardDeleteUser(userId);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        UUID userId = easyRandom.nextObject(UUID.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> target.deleteUser(userId, userId))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteUser_whenCurrentUserDiffers_shouldThrowUnauthorizedAccess() {
        User user = randomUser(true);
        UUID userId = user.getId();
        UUID currentUserId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> target.deleteUser(userId, currentUserId))
            .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void deleteUser_shouldSoftDeleteAndSchedulePurge() {
        User user = randomUser(true);
        UUID userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        User result = target.deleteUser(userId, userId);

        assertThat(result.isActive()).isFalse();
        assertThat(result.getDeletedAt()).isNotNull();
        verify(userRepository).save(user);
    }

    private User randomUser(boolean active) {
        UUID id = easyRandom.nextObject(UUID.class);
        return User.builder()
            .id(id)
            .email("user-" + id + "@example.com")
            .nickname("nick-" + id.toString().substring(0, 8))
            .password("password123")
            .createdAt(LocalDateTime.now())
            .isActive(active)
            .build();
    }
}
