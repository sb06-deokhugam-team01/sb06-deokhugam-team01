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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;

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
        // given
        String email = "user@example.com";
        String nickname = "tester";
        String password = "password123";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        // when
        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            User result = userService.createUser(email, nickname, password);

            // then
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getNickname()).isEqualTo(nickname);
            assertThat(result.isActive()).isTrue();
            assertThat(result.getId()).isNotNull();
            verify(userRepository).existsByEmail(email);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void registerUser_whenEmailAlreadyExists_shouldThrowInvalidUserException() {
        String email = "duplicate@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            assertThatThrownBy(() -> userService.createUser(email, "tester", "password123"))
                .isInstanceOf(InvalidUserException.class);
        }
    }

    @Test
    void login_shouldReturnActiveUserWhenCredentialsMatch() {
        User user = randomUser(true);
        String email = user.getEmail();
        String password = user.getPassword();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            User result = userService.login(email, password);

            assertThat(result).isEqualTo(user);
        }
    }

    @Test
    void login_withWrongPassword_shouldThrowInvalidUserException() {
        User user = randomUser(true);
        String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            assertThatThrownBy(() -> userService.login(email, "wrong"))
                .isInstanceOf(InvalidUserException.class);
        }
    }

    @Test
    void getUser_shouldReturnActiveUserById() {
        User user = randomUser(true);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            User result = userService.getUser(userId);

            assertThat(result).isEqualTo(user);
        }
    }

    @Test
    void getUser_whenUserInactive_shouldThrowInvalidUserException() {
        User user = randomUser(false);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(InvalidUserException.class);
        }
    }

    @Test
    void deactivateUser_shouldMarkUserAsInactive() {
        User user = randomUser(true);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            User result = userService.deactivateUser(userId);

            assertThat(result.isActive()).isFalse();
            verify(userRepository).save(user);
        }
    }

    @Test
    void updateUser_shouldUpdateNicknameOnly() {
        User user = randomUser(true);
        UUID userId = user.getId();
        String newNickname = "renamedUser";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            User result = userService.updateUser(userId, newNickname);

            assertThat(result.getNickname()).isEqualTo(newNickname);
            verify(userRepository).save(user);
        }
    }

    @Test
    void deleteUser_shouldRemoveUserFromRepository() {
        User user = randomUser(true);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            userService.hardDeleteUser(userId);

            verify(userRepository, times(1)).delete(user);
        }
    }

    @Test
    void deleteUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        UUID userId = easyRandom.nextObject(UUID.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Test
    void deleteUser_shouldSoftDeleteAndSchedulePurge() {
        User user = randomUser(true);
        UUID userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(returnFirstArgument);

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            User result = userService.deleteUser(userId);

            assertThat(result.isActive()).isFalse();
            assertThat(result.getDeletedAt()).isNotNull();
            verify(userRepository).save(user);
        }
    }

    @Test
    void purgeDeletedUsersBefore_shouldDelegateToRepository() {
        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);

        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            userService.purgeDeletedUsersBefore(cutoff);
        }

        verify(userRepository).deleteAllSoftDeletedBefore(cutoffCaptor.capture());
        assertThat(cutoffCaptor.getValue()).isEqualTo(cutoff);
    }

    @Test
    void createUser_withInvalidEmail_shouldThrow() {
        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            assertThatThrownBy(() -> userService.createUser("not-an-email", "nick", "password123"))
                .isInstanceOf(ConstraintViolationException.class);
        }
    }

    @Test
    void createUser_withShortPassword_shouldThrow() {
        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            assertThatThrownBy(() -> userService.createUser("user@example.com", "nick", "short"))
                .isInstanceOf(ConstraintViolationException.class);
        }
    }

    @Test
    void createUser_withInvalidNickname_shouldThrow() {
        try (ValidatedService svc = validatedService()) {
            UserService userService = svc.service();
            assertThatThrownBy(() -> userService.createUser("user@example.com", "n", "password123"))
                .isInstanceOf(ConstraintViolationException.class);
        }
    }

    private User randomUser(boolean active) {
        UUID id = easyRandom.nextObject(UUID.class);
        User user = User.builder()
            .id(id)
            .email("user-" + id + "@example.com")
            .nickname("nick-" + id.toString().substring(0, 8))
            .password("password123")
            .createdAt(LocalDateTime.now())
            .isActive(active)
            .build();
        return user;
    }

    private ValidatedService validatedService() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Validator jakartaValidator = validator.getValidator();
        ProxyFactory factory = new ProxyFactory(target);
        factory.addAdvice(new MethodValidationInterceptor(jakartaValidator));
        UserService proxied = (UserService) factory.getProxy();
        return new ValidatedService(proxied, validator);
    }

    private record ValidatedService(UserService service, LocalValidatorFactoryBean validator)
        implements AutoCloseable {
        @Override
        public void close() {
            validator.destroy();
        }
    }
}
