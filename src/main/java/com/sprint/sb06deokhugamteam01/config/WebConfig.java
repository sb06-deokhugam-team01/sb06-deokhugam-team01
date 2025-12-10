package com.sprint.sb06deokhugamteam01.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestLoggingFilter());
        registrationBean.addUrlPatterns("/*"); // 모든 요청에 필터 적용
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 필터 체인에서 가장 먼저 실행
        return registrationBean;
    }
}