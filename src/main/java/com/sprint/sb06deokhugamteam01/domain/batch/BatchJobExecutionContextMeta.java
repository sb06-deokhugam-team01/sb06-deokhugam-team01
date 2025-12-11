package com.sprint.sb06deokhugamteam01.domain.batch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "batch_job_execution_context")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BatchJobExecutionContextMeta {

    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "job_execution_id")
    private BatchJobExecutionMeta jobExecution;

    @Column(name = "short_context", length = 2500, nullable = false)
    private String shortContext;

    @Lob
    @Column(name = "serialized_context")
    private String serializedContext;
}
