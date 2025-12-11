package com.sprint.sb06deokhugamteam01.config;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import jakarta.annotation.PostConstruct;

/**
 * 애플리케이션 기동 시 Spring Batch 메타데이터 스키마를 적용한다.
 * 배치 메타 테이블/시퀀스가 없을 때 자동으로 생성되도록 한다.
 */
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class BatchSchemaInitializerConfig {

    private final DataSource dataSource;

    @PostConstruct
    public void initBatchSchema() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema-batch.sql"));
        populator.setContinueOnError(true); // 이미 존재하는 경우 무시
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}
