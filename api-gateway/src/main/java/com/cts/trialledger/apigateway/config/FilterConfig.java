package com.cts.trialledger.apigateway.config;

import com.cts.trialledger.apigateway.filter.AuditLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuditLoggingFilter> auditFilter(
            AuditLoggingFilter auditLoggingFilter) {

        FilterRegistrationBean<AuditLoggingFilter> registration =
                new FilterRegistrationBean<>(auditLoggingFilter);

        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE); // Run after security filters
        return registration;
    }
}
