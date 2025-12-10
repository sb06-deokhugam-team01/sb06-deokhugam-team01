package com.sprint.sb06deokhugamteam01.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Profile("!test")
public class DeleteDeactivatedJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DeleteDeactivatedDataService deleteDeactivatedDataService;

    @Bean
    public Job deleteDeactivatedJob() {
        return new JobBuilder("deleteDeactivatedJob", jobRepository)
                .start(deleteDeactivatedStep())
                .build();
    }

    @Bean
    public Step deleteDeactivatedStep() {
        return new StepBuilder("deleteDeactivatedStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    deleteDeactivatedDataService.deleteDeactivatedBooks();
                    deleteDeactivatedDataService.deleteDeactivatedUsers();
                    deleteDeactivatedDataService.deleteDeactivatedComments();
                    deleteDeactivatedDataService.deleteDeactivatedReviews();
                    deleteDeactivatedDataService.deleteDeactivatedNotifications();
                    return null;
                }, transactionManager)
                .build();
    }
}
