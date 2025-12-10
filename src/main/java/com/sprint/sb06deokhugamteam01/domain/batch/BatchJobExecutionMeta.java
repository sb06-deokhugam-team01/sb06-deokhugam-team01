package com.sprint.sb06deokhugamteam01.domain.batch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "batch_job_execution")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BatchJobExecutionMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_execution_id")
    private Long id;

    @Column
    private Long version;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_instance_id")
    private BatchJobInstanceMeta jobInstance;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(length = 10)
    private String status;

    @Column(name = "exit_code", length = 2500)
    private String exitCode;

    @Column(name = "exit_message", length = 2500)
    private String exitMessage;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "job_configuration_location", length = 2500)
    private String jobConfigurationLocation;
}
