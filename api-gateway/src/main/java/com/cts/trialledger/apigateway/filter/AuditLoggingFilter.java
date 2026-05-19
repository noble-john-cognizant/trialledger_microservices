package com.cts.trialledger.apigateway.filter;

import com.cts.trialledger.apigateway.service.AuditService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLoggingFilter implements Filter {  // javax/jakarta servlet Filter

    private final AuditService auditService;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        long startTime = System.currentTimeMillis();

        try {
            // PRE: let the request continue to the gateway → downstream service
            chain.doFilter(request, response);
        } finally {
            // POST: always runs after response is written, even on exception
            long duration = System.currentTimeMillis() - startTime;
            auditService.saveAudit(request, response.getStatus(), duration);

            log.info("[AUDIT] method={} uri={} status={} duration={}ms user={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    SecurityContextHolder.getContext().getAuthentication() != null
                            ? SecurityContextHolder.getContext().getAuthentication().getName()
                            : "anonymous"
            );
        }
    }
}
