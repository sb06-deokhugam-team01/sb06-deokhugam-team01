package com.sprint.sb06deokhugamteam01.domain.batch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "batch_job_instance",
        uniqueConstraints = @UniqueConstraint(name = "batch_job_instance_uq", columnNames = {"job_name", "job_key"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BatchJobInstanceMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_instance_id")
    private Long id;

    @Column
    private Long version;

    @Column(name = "job_name", length = 100, nullable = false)
    private String jobName;

    @Column(name = "job_key", length = 32, nullable = false)
    private String jobKey;
}
