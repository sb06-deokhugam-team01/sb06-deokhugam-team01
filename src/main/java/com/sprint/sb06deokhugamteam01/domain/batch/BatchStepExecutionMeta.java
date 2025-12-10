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
@Table(name = "batch_step_execution")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BatchStepExecutionMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_execution_id")
    private Long id;

    @Column(nullable = false)
    private Long version;

    @Column(name = "step_name", length = 100, nullable = false)
    private String stepName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_execution_id")
    private BatchJobExecutionMeta jobExecution;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(length = 10)
    private String status;

    @Column(name = "commit_count")
    private Long commitCount;

    @Column(name = "read_count")
    private Long readCount;

    @Column(name = "filter_count")
    private Long filterCount;

    @Column(name = "write_count")
    private Long writeCount;

    @Column(name = "read_skip_count")
    private Long readSkipCount;

    @Column(name = "write_skip_count")
    private Long writeSkipCount;

    @Column(name = "process_skip_count")
    private Long processSkipCount;

    @Column(name = "rollback_count")
    private Long rollbackCount;

    @Column(name = "exit_code", length = 2500)
    private String exitCode;

    @Column(name = "exit_message", length = 2500)
    private String exitMessage;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
