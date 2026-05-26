package com.cts.trialledger.provenance.security;

import com.cts.trialledger.provenance.model.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class GatewayHeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Block any request that did NOT come through the API gateway.
        // The gateway always injects X-Gateway-Auth: true before forwarding.
        // Without this check, anyone who knows the service URL can bypass auth.
        String gatewayMarker = request.getHeader("X-Gateway-Auth");
        if (!"true".equals(gatewayMarker)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Direct access not allowed\",\"status\":403}");
            return;
        }

        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        String role = request.getHeader("X-User-Role");
        String email = request.getHeader("X-User-Email");
        String name = request.getHeader("X-User-Name");

        if (role != null && !role.isBlank()) {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId,  // principal
                            email,   // credentials
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    );

            auth.setDetails(new UserDetails(userId, name, role, email));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}
