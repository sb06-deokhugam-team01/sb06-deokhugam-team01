package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.config.JpaAuditingConfig;
import com.sprint.sb06deokhugamteam01.config.QueryDslConfig;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchUserRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.dto.User.request.PowerUserRequest;
import com.sprint.sb06deokhugamteam01.repository.user.BatchUserRatingRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { QueryDslConfig.class }
))
@ActiveProfiles("test")
public class BatchUserRatingRepositoryTest {

    @Autowired
    private BatchUserRatingRepository batchUserRatingRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        batchUserRatingRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalDate today = now.toLocalDate();
        LocalDate start = today.minusDays(6);

        createRating(100.0, now.minusHours(1), start, today);
        createRating(90.0, now.minusHours(2), start, today);
        createRating(80.0, now.minusHours(3), start, today);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("첫 페이지 (커서 X)")
    void getList_FirstPage() {
        // given
        PowerUserRequest request = PowerUserRequest.builder().period("WEEKLY")
                .direction("DESC").limit(10).build();

        // when
        Slice<BatchUserRating> result = batchUserRatingRepository.getBatchUserRatingList(request);

        // then
        assertThat(result.getContent()).hasSize(3);
    }
    @Test
    @DisplayName("커서 페이징")
    void getList_NextPage() {
        // given
        List<BatchUserRating> all = batchUserRatingRepository.findAll();

        BatchUserRating target = all.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElseThrow();

        PowerUserRequest request = PowerUserRequest.builder().direction("DESC").period("WEEKLY")
                .limit(10).cursor(target.getId().toString()).after(target.getCreatedAt()).build();
        request.setPeriodStart(target.getPeriodEnd());

        // when
        Slice<BatchUserRating> result = batchUserRatingRepository.getBatchUserRatingList(request);

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    private void createRating(Double score, LocalDateTime createdAt, LocalDate start, LocalDate end) {
        User user = User.builder()
                .email(UUID.randomUUID() + "@test.com")
                .nickname("user").build();
        entityManager.persist(user);

        BatchUserRating rating = BatchUserRating.builder()
                .user(user).score(score).rank(1).periodType(PeriodType.WEEKLY)
                .periodStart(start).periodEnd(end).createdAt(createdAt).build();
        entityManager.persist(rating);

        entityManager.createNativeQuery("UPDATE batch_user_rating SET created_at =?1 WHERE id =?2")
                .setParameter(1, createdAt)
                .setParameter(2, rating.getId())
                .executeUpdate();
    }
}
