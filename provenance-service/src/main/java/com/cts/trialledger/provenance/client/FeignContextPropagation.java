package com.cts.trialledger.provenance.client;

import com.cts.trialledger.provenance.model.UserDetails;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignContextPropagation {

    @Bean
    public RequestInterceptor userContextInterceptor() {
        return template -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated()
                    && auth.getDetails() instanceof UserDetails user) {

                // User-initiated call — propagate identity downstream
                template.header("X-User-Id", user.getUserId().toString());
                template.header("X-User-Role", user.getRole());
                template.header("X-User-Email", user.getEmail());
                template.header("X-User-Name", user.getName());
                template.header("X-Gateway-Auth", "true");

            }
        };
    }
}
