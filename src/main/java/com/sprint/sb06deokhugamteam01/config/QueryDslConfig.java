package com.sprint.sb06deokhugamteam01.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.sb06deokhugamteam01.Sb06DeokhugamTeam01Application;
import io.github.openfeign.querydsl.jpa.spring.repository.config.EnableQuerydslRepositories;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableQuerydslRepositories(basePackageClasses = Sb06DeokhugamTeam01Application.class)
public class QueryDslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
