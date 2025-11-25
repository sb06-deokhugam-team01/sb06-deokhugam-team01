package com.sprint.sb06deokhugamteam01.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.exception.user.InvalidUserException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.UserRepository;
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
    private UserServiceImpl userService;

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
        // given
        String email = easyRandom.nextObject(String.class);
        String nickname = easyRandom.nextObject(String.class);
        String password = easyRandom.nextObject(String.class);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        // when
        User result = userService.createUser(email, nickname, password);

        // then
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getId()).isNotNull();
        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_whenEmailAlreadyExists_shouldThrowInvalidUserException() {
        String email = easyRandom.nextObject(String.class);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(email, "tester", "pw"))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void login_shouldReturnActiveUserWhenCredentialsMatch() {
        User user = randomUser(true);
        String email = user.getEmail();
        String password = user.getPassword();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.login(email, password);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void login_withWrongPassword_shouldThrowInvalidUserException() {
        User user = randomUser(true);
        String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.login(email, "wrong"))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void getUser_shouldReturnActiveUserById() {
        User user = randomUser(true);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUser(userId);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUser_whenUserInactive_shouldThrowInvalidUserException() {
        User user = randomUser(false);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void deactivateUser_shouldMarkUserAsInactive() {
        User user = randomUser(true);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        User result = userService.deactivateUser(userId);

        assertThat(result.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldUpdateNicknameAndPassword() {
        User user = randomUser(true);
        UUID userId = user.getId();
        String newNickname = easyRandom.nextObject(String.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        User result = userService.updateUser(userId, newNickname);

        assertThat(result.getNickname()).isEqualTo(newNickname);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_shouldRemoveUserFromRepository() {
        User user = randomUser(true);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        UUID userId = easyRandom.nextObject(UUID.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
            .isInstanceOf(UserNotFoundException.class);
    }

    private User randomUser(boolean active) {
        User user = easyRandom.nextObject(User.class);
        if (active) {
            user.activate();
        } else {
            user.deactivate();
        }
        return user;
    }
}
