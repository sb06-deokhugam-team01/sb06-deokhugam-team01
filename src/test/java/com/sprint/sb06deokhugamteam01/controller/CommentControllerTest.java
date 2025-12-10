package com.sprint.sb06deokhugamteam01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.sb06deokhugamteam01.dto.comment.CommentDto;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentListRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.response.CursorPageCommentResponse;
import com.sprint.sb06deokhugamteam01.service.comment.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CommentService commentService;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("댓글 등록 성공")
    void createComment_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        String content = "댓".repeat(500);
        String userNickname = "유저";

        CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, content);

        CommentDto response = CommentDto.builder().id(UUID.randomUUID())
                .reviewId(reviewId).userId(userId).userNickname(userNickname).content(content)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        given(commentService.createComment(any(CommentCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(
                        post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.userNickname").value(userNickname));
    }
    @Test
    @DisplayName("리뷰 아이디 validation 실패로 댓글 등록 실패")
    void createComment_InvalidReviewId_Fail() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        String content = "댓글";

        CommentCreateRequest invalidRequest = new CommentCreateRequest(null, userId, content);

        // when & then
        mockMvc.perform(
                        post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest());
    }
    @ParameterizedTest
    @MethodSource("provideInvalidContents")
    @DisplayName("댓글 내용 validation 실패로 댓글 등록 실패")
    void createComment_InvalidContent_Fail(String invalidContent) throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        CommentCreateRequest invalidRequest = new CommentCreateRequest(reviewId, userId, invalidContent);

        // when & then
        mockMvc.perform(
                        post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        String userNickname = "유저";
        String content = "!".repeat(500);

        CommentUpdateRequest request = new CommentUpdateRequest(content);
        
        CommentDto response = CommentDto.builder().id(commentId)
                .reviewId(reviewId).userId(userId).userNickname(userNickname).content(content)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        given(commentService.updateComment(commentId, userId, request)).willReturn(response);


        // when & then
        mockMvc.perform(
                patch("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Deokhugam-Request-User-ID", userId)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.userNickname").value(userNickname));
    }
    @Test
    @DisplayName("헤더 요청자 ID 누락으로 댓글 수정 실패")
    void updateComment_MissingHeader_Fail() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        String content = "댓글";

        CommentUpdateRequest request = new CommentUpdateRequest(content);

        // when & then
        mockMvc.perform(
                patch("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
    @ParameterizedTest
    @MethodSource("provideInvalidContents")
    @DisplayName("댓글 내용 validation 실패로 댓글 수정 실패")
    void updateComment_InvalidContent_Fail(String invalidContent) throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CommentUpdateRequest invalidRequest = new CommentUpdateRequest(invalidContent);

        // when & then
        mockMvc.perform(
                patch("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Deokhugam-Request-User-ID", userId)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 논리 삭제 성공")
    void deleteComment_Success() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when & then
        mockMvc.perform(
                delete("/api/comments/{commentId}", commentId)
                        .header("Deokhugam-Request-User-ID", userId)
                )
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("헤더 요청자 ID 누락으로 댓글 논리 삭제 실패")
    void deleteComment_MissingHeader_Fail() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(
                delete("/api/comments/{commentId}", commentId)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 물리 삭제 성공")
    void hardDeleteComment_Success() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when & then
        mockMvc.perform(
                delete("/api/comments/{commentId}/hard", commentId)
                        .header("Deokhugam-Request-User-ID", userId)
                )
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("헤더 요청자 ID 누락으로 댓글 물리 삭제 실패")
    void hardDeleteComment_MissingHeader_Fail() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(
                        delete("/api/comments/{commentId}/hard", commentId)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 상세 정보 조회 성공")
    void getComment_Success() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        String userNickname = "유저";
        String content = "댓글";

        CommentDto response = CommentDto.builder().id(commentId)
                .reviewId(reviewId).userId(userId).userNickname(userNickname).content(content)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        given(commentService.getComment(commentId)).willReturn(response);

        // when & then
        mockMvc.perform(
                get("/api/comments/{commentId}", commentId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.userNickname").value(userNickname));
    }

    @Test
    @DisplayName("리뷰 댓글 목록 조회 성공")
    void getComments_Success() throws Exception {
        // given
        UUID reviewId = UUID.randomUUID();
        UUID cursor = UUID.randomUUID();
        LocalDateTime after = LocalDateTime.now().minusDays(1);

        UUID nextCursor = UUID.randomUUID();
        LocalDateTime nextAfter = LocalDateTime.now().minusDays(2);

        CursorPageCommentResponse response = new CursorPageCommentResponse(
              List.of(), nextCursor, nextAfter, 50, 100L,true
        );

        given(commentService.getComments(any(CommentListRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(
                get("/api/comments")
                        .param("reviewId", reviewId.toString())
                        .param("cursor", cursor.toString())
                        .param("after", after.toString())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(50))
                .andExpect(jsonPath("$.nextCursor").value(nextCursor.toString()))
                .andExpect(jsonPath("$.hasNext").value(true));
    }
    @Test
    @DisplayName("리뷰 댓글 목록 조회 시 파라미터 누락되면 기본값 적용 (DESC, limit=50)")
    void getComments_DefaultValue_Check() throws Exception {
        // given
        given(commentService.getComments(any())).willReturn(any());

        // when
        mockMvc.perform(
                get("/api/comments")
                        .param("reviewId", UUID.randomUUID().toString())) // 필수값
                .andExpect(status().isOk());

        // then
        // CommentService의 getComments가 호출될 때 들어간 인자 캡처
        ArgumentCaptor<CommentListRequest> captor = ArgumentCaptor.forClass(CommentListRequest.class);
        verify(commentService).getComments(captor.capture());

        CommentListRequest actualRequest = captor.getValue();
        assertThat(actualRequest.limit()).isEqualTo(50);
        assertThat(actualRequest.direction()).isEqualTo(Sort.Direction.DESC);
    }
    @Test
    @DisplayName("리뷰 아이디 누락으로 목록 조회 실패")
    void getComments_MissingReviewId_Fail() throws Exception {
        // when & then
        mockMvc.perform(
                get("/api/comments")
                        .param("limit", "10") // 다른 파라미터는 정상적으로 포함
                        .param("direction", "ASC")
                )
                .andExpect(status().isBadRequest());
    }
    @ParameterizedTest
    @MethodSource("provideInvalidAfter")
    @DisplayName("after validation 실패로 목록 조회 실패")
    void getComments_InvalidAfter_Fail(LocalDateTime invalidAfter) throws Exception {
        // when & then
        mockMvc.perform(
                        get("/api/comments")
                        .param("after", invalidAfter.toString())
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("limit validation 실패로 목록 조회 실패")
    void getComments_InvalidLimit_Fail() throws Exception {
        // when & then
        mockMvc.perform(
                        get("/api/comments")
                        .param("limit", "0")
                )
                .andExpect(status().isBadRequest());
    }

    private static Stream<String> provideInvalidContents() {
        return Stream.of(
                "", // 빈 문자열
                "     ", // 공백
                "댓".repeat(501) // 길이 초과
        );
    }
    private static Stream<LocalDateTime> provideInvalidAfter() {
        return Stream.of(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)
        );
    }
}
