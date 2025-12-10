package com.sprint.sb06deokhugamteam01.batch;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Profile("!test")
@RequiredArgsConstructor
public class DailyBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job ratingAggregationJob;
    private final Job deleteDeactivatedJob;

    /**
     * 매일 새벽 2시에 전일 데이터를 기준으로 집계 + 비활성 유저 삭제를 실행한다.
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void runDaily() throws Exception {
        LocalDate targetDate = LocalDate.now();

        JobParameters params = new JobParametersBuilder()
                .addString("targetDate", targetDate.toString())
                .addLong("run.id", System.currentTimeMillis()) // 재실행 가능하도록 유니크 파라미터
                .toJobParameters();

        jobLauncher.run(ratingAggregationJob, params);
        jobLauncher.run(deleteDeactivatedJob, params);
    }
}
