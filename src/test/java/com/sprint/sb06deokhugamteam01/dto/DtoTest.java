package com.sprint.sb06deokhugamteam01.dto;

import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchUserRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.dto.User.request.PowerUserRequest;
import com.sprint.sb06deokhugamteam01.dto.User.response.CursorPageResponsePowerUserDto;
import com.sprint.sb06deokhugamteam01.dto.User.response.PowerUserDto;
import com.sprint.sb06deokhugamteam01.dto.book.PopularBookDto;
import com.sprint.sb06deokhugamteam01.dto.book.naver.BookData;
import com.sprint.sb06deokhugamteam01.dto.book.naver.NaverBookSearchResponse;
import com.sprint.sb06deokhugamteam01.dto.notification.PageNotificationRequest;
import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoTest {
    @Test
    @DisplayName("ErrorDto 테스트 - RootException")
    void errorDtoTest_RootException() {
        // given
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        RootException e = new RootException(errorCode, Map.of());

        // when
        ResponseEntity<ErrorDto> result = ErrorDto.toResponseEntity(e);

        // then
        assertThat(result.getBody().status()).isEqualTo(errorCode.getStatus());
    }
    @Test
    @DisplayName("ErrorDto 테스트 - Exception")
    void errorDtoTest_Exception() {
        // given
        Exception e = new Exception();

        // when
        ResponseEntity<ErrorDto> result = ErrorDto.toResponseEntity(e);

        // then
        assertThat(result.getBody().status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    @Test
    @DisplayName("PopularBookDto 테스트")
    void bookDtoTest_BookDto() {
        // given
        Book book = Book.builder().title("책").author("저자").description("설명")
                .publishedDate(LocalDate.now()).publisher("출판사").isbn("123").build();
        book.updateThumbnailUrl("url");
        ReflectionTestUtils.setField(book, "id", UUID.randomUUID());
        BatchBookRating batchBookRating = BatchBookRating.builder().id(UUID.randomUUID())
                .periodType(PeriodType.DAILY).rank(1).score(1).createdAt(LocalDateTime.now()).build();

        //when
        PopularBookDto result = PopularBookDto.fromEntity(book, batchBookRating);

        // then
        assertThat(result.author()).isEqualTo("저자");
        assertThat(result.rank()).isEqualTo(1L);
    }
    @Test
    @DisplayName("PopularBookDto 테스트 - String 파라미터 포함")
    void bookDtoTest_BookDto_WithString() {
        // given
        Book book = Book.builder().title("책").author("저자").description("설명")
                .publishedDate(LocalDate.now()).publisher("출판사").isbn("123").build();
        book.updateThumbnailUrl("url");
        ReflectionTestUtils.setField(book, "id", UUID.randomUUID());
        BatchBookRating batchBookRating = BatchBookRating.builder().id(UUID.randomUUID())
                .periodType(PeriodType.DAILY).rank(1).score(1).createdAt(LocalDateTime.now()).build();
        String imageUrl = "url";

        //when
        PopularBookDto result = PopularBookDto.fromEntityWithImageUrl(book, batchBookRating, imageUrl);

        // then
        assertThat(result.author()).isEqualTo("저자");
        assertThat(result.thumbnailUrl()).isEqualTo(imageUrl);
    }
    @Test
    @DisplayName("PowerUserDto 테스트")
    void powerUserDtoTest() {
        // given
        User user = User.builder().id(UUID.randomUUID()).nickname("유저").build();
        BatchUserRating rating = BatchUserRating.builder().user(user)
                .periodType(PeriodType.DAILY).createdAt(LocalDateTime.now())
                .rank(1).score(1).reviewPopularitySum(1).likesMade(1).commentsMade(1).build();

        // when
        PowerUserDto dto = PowerUserDto.fromBatchUserRating(rating);

        // then
        assertThat(dto.nickname()).isEqualTo(user.getNickname());
        assertThat(dto.rank()).isEqualTo(1L);
    }
    @Test
    @DisplayName("CursorPageResponsePowerUserDto 테스트")
    void cursorPageResponsePowerUserDtoTest() {
        CursorPageResponsePowerUserDto dto = new CursorPageResponsePowerUserDto(
                List.of(), "nextCursor", "nextAfter", 3, 100, true
        );

        assertThat(dto.totalElements()).isEqualTo(100);
    }

    @Test
    @DisplayName("BookData 테스트")
    void BookDataTest() {
        // given & when
        BookData bookData = new BookData("책", "link", "image", "저자",
                "discount", "publisher", "20251210", "isbn", "description");

        // then
        assertThat(bookData.getPublishedDate()).isEqualTo(LocalDate.of(2025, 12, 10));
    }
    @Test
    @DisplayName("BookData 테스트 - invalid pubdate")
    void BookDataTest_InvalidPubdate() {
        // given & when
        BookData bookData = new BookData("책", "link", "image", "저자",
                "discount", "publisher", "222", "isbn", "description");

        // then
        assertThat(bookData.getPublishedDate()).isNull();
    }
    @Test
    @DisplayName("NaverBookSearchResponse 테스트")
    void NaverBookSearchResponseTest() {
        // given & when
        NaverBookSearchResponse response = new NaverBookSearchResponse(
                "20220202", 10, 10, 10, new BookData[]{}
        );

        // then
        assertThat(response.items()).isEmpty();
    }
    @Test
    @DisplayName("PageNotificationRequest 테스트")
    void PageNotificationRequestTest() {
        // given & when
        PageNotificationRequest request = new PageNotificationRequest(
                UUID.randomUUID(), "ASC", "cursor", LocalDateTime.now(), 10
        );

        // then
        assertThat(request.isAscending()).isTrue();
    }
    @Test
    @DisplayName("PowerUserRequest 테스트")
    void PowerUserRequestTest() {
        // given & when
        LocalDateTime time = LocalDateTime.now();
        PowerUserRequest request = new PowerUserRequest(
                "monthly", "ASC", "cursor", time, 10
        );
        request.toPeriodType();
        request.setPeriodStart(LocalDate.now());

        // then
        assertThat(request.after()).isEqualTo(time);
    }
}
