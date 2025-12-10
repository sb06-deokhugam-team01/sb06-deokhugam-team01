package com.sprint.sb06deokhugamteam01.domain.batch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@Table(name = "batch_job_execution_params")
@IdClass(BatchJobExecutionParamId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BatchJobExecutionParamsMeta {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "job_execution_id")
    private BatchJobExecutionMeta jobExecution;

    @Id
    @Column(name = "parameter_name", length = 100, nullable = false)
    private String parameterName;

    @Column(name = "parameter_type", length = 100, nullable = false)
    private String parameterType;

    @Column(name = "parameter_value", length = 2500)
    private String parameterValue;

    @Column(name = "identifying", length = 1, nullable = false)
    private String identifying;
}
