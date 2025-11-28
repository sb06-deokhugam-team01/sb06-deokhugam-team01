package com.sprint.sb06deokhugamteam01.controller;

import com.sprint.sb06deokhugamteam01.dto.User.request.UserRegisterRequest;
import com.sprint.sb06deokhugamteam01.dto.User.response.UserDto;
import com.sprint.sb06deokhugamteam01.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        log.info("Received user registration request: {}", userRegisterRequest);
        UserDto userDto = UserDto.from(userService.createUser(userRegisterRequest));
        log.info("User registered successfully: {}", userDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        log.info("Received user login request: {}", userRegisterRequest);
        UserDto userDto = UserDto.from(userService.login(userRegisterRequest.email(), userRegisterRequest.password()));
        log.info("User logged in successfully: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID userId){
        log.info("Received user get request: {}", userId);
        UserDto userDto = UserDto.from(userService.getUser(userId));
        log.info("User retrieved successfully: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable UUID userId, @RequestHeader("Deokhugam-Request-User-ID") UUID currentUserId) {
        log.info("Received user delete request: {}", userId);
        UserDto userDto = UserDto.from(userService.deleteUser(userId, currentUserId));
        log.info("User deleted successfully: {}", userDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID userId, @NotNull @Size(min = 2, max = 20) @RequestBody String nickname,
        @RequestHeader("Deokhugam-Request-User-ID") UUID currentUserId) {
        log.info("Received user update request for user: {} with nickname: {}", userId, nickname);
        UserDto userDto = UserDto.from(userService.updateUser(userId, nickname, currentUserId));
        log.info("User updated successfully: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable UUID userId){
        log.info("Received hard delete request for user: {}", userId);
        userService.hardDeleteUser(userId);
        log.info("User hard deleted successfully: {}", userId);
        return ResponseEntity.noContent().build();
    }

}
