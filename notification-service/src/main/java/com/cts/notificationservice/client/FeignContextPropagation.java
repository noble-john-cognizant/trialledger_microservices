package com.cts.notificationservice.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignContextPropagation {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                String gatewayAuth = request.getHeader("X-Gateway-Auth");
                String userId = request.getHeader("X-User-Id");
                String role = request.getHeader("X-User-Role");
                String email = request.getHeader("X-User-Email");
                String name = request.getHeader("X-User-Name");

                if (gatewayAuth != null) template.header("X-Gateway-Auth", gatewayAuth);
                if (userId != null) template.header("X-User-Id", userId);
                if (role != null) template.header("X-User-Role", role);
                if (email != null) template.header("X-User-Email", email);
                if (name != null) template.header("X-User-Name", name);
            }
        };
    }
}
