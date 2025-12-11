package com.sprint.sb06deokhugamteam01.batch;

import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Profile("!test")
public class RatingBatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RatingAggregationService ratingAggregationService;

    @Bean
    public Job ratingAggregationJob() {
        return new JobBuilder("ratingAggregationJob", jobRepository)
                .start(ratingAggregationStep())
                .build();
    }

    @Bean
    public Step ratingAggregationStep() {
        return new StepBuilder("ratingAggregationStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate targetDate = Optional.ofNullable(
                                    chunkContext.getStepContext().getJobParameters().get("targetDate"))
                            .map(Object::toString)
                            .map(LocalDate::parse)
                            .orElse(LocalDate.now());

                    ratingAggregationService.aggregateAllPeriods(targetDate);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
