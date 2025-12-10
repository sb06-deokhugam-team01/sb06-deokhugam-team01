package com.sprint.sb06deokhugamteam01.domain.batch;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BatchJobExecutionParamId implements Serializable {
    // Must match entity field name; type is the identifier type of BatchJobExecutionMeta
    private Long jobExecution;
    private String parameterName;
}
