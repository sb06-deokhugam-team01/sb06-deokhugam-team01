package com.sprint.sb06deokhugamteam01.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.User.request.UserRegisterRequest;
import com.sprint.sb06deokhugamteam01.service.user.UserService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users - 성공 시 200 OK와 사용자 정보 반환")
    void createUser_shouldReturnUserDto() throws Exception {
        UUID userId = UUID.randomUUID();
        UserRegisterRequest request = new UserRegisterRequest("user@example.com", "password123", "tester");
        User user = sampleUser(userId, request.email(), request.nickname(), request.password());

        when(userService.createUser(any(UserRegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value(request.email()))
            .andExpect(jsonPath("$.nickname").value(request.nickname()));

        verify(userService).createUser(any(UserRegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/users - 잘못된 요청 시 400 Bad Request")
    void createUser_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        UserRegisterRequest invalidRequest = new UserRegisterRequest("not-an-email", "short", "");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users/login - 성공 시 200 OK와 사용자 정보 반환")
    void loginUser_shouldReturnUserDto() throws Exception {
        UUID userId = UUID.randomUUID();
        UserRegisterRequest request = new UserRegisterRequest("user@example.com", "password123", "tester");
        User user = sampleUser(userId, request.email(), request.nickname(), request.password());

        when(userService.login(request.email(), request.password())).thenReturn(user);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value(request.email()))
            .andExpect(jsonPath("$.nickname").value("tester"));

        verify(userService).login(request.email(), request.password());
    }

    @Test
    @DisplayName("POST /api/users/login - 잘못된 요청 시 400 Bad Request")
    void loginUser_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        UserRegisterRequest invalidRequest = new UserRegisterRequest("bad-email", "", "");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("GET /api/users/{userId} - 사용자 조회 성공")
    void getUser_shouldReturnUser() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = sampleUser(userId, "user@example.com", "tester", "password123");

        when(userService.getUser(userId)).thenReturn(user);

        mockMvc.perform(get("/api/users/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value(user.getEmail()))
            .andExpect(jsonPath("$.nickname").value(user.getNickname()));

        verify(userService).getUser(userId);
    }

    @Test
    @DisplayName("PATCH /api/users/{userId} - 닉네임 업데이트 성공")
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        String nickname = "nickname";
        User updated = sampleUser(userId, "user@example.com", nickname, "password123");

        when(userService.updateUser(eq(userId), any(), eq(userId))).thenReturn(updated);

        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.TEXT_PLAIN)
                .content(nickname))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value(nickname));

        ArgumentCaptor<String> nicknameCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService).updateUser(eq(userId), nicknameCaptor.capture(), eq(userId));
        assertThat(nicknameCaptor.getValue()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("PATCH /api/users/{userId} - 허가되지 않은 접근 시 403 반환")
    void updateUser_unauthorizedAccess_shouldReturnForbidden() throws Exception {
        UUID userId = UUID.randomUUID();
        String nickname = "nickname";
        UUID currentUserId = UUID.randomUUID();

        when(userService.updateUser(eq(userId), any(), eq(currentUserId))).thenThrow(new RuntimeException("Unauthorized"));

        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.TEXT_PLAIN)
                .content(nickname))
            .andExpect(status().isForbidden());

        ArgumentCaptor<String> nicknameCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService).updateUser(eq(userId), nicknameCaptor.capture(), eq(currentUserId));
        assertThat(nicknameCaptor.getValue()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - 소프트 삭제 성공")
    void deleteUser_shouldReturnDeletedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        User deleted = sampleUser(userId, "user@example.com", "tester", "password123");

        when(userService.deleteUser(userId, userId)).thenReturn(deleted);

        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()));

        verify(userService).deleteUser(userId, userId);
    }

    @Test
    @DisplayName("DELETE /api/users/{userId}/hard - 하드 삭제 성공 시 204 반환")
    void hardDeleteUser_shouldReturnNoContent() throws Exception {
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).hardDeleteUser(userId);

        mockMvc.perform(delete("/api/users/{userId}/hard", userId))
            .andExpect(status().isNoContent());

        verify(userService).hardDeleteUser(userId);
    }

    private User sampleUser(UUID id, String email, String nickname, String password) {
        return User.builder()
            .id(id)
            .email(email)
            .nickname(nickname)
            .password(password)
            .createdAt(LocalDateTime.now())
            .isActive(true)
            .build();
    }
}
