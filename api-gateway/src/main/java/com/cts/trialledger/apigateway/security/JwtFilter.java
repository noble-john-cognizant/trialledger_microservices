package com.cts.trialledger.apigateway.security;

import com.cts.trialledger.apigateway.config.MutableHttpServletRequest;
import com.cts.trialledger.apigateway.dto.ErrorResponse;
import com.cts.trialledger.apigateway.entity.User;
import com.cts.trialledger.apigateway.model.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;


    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = null;
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            accessToken = authorization.substring(7);
        }
        if (accessToken != null) {
            try {
                if (jwtUtil.validateToken(accessToken)) {
                    Claims claims = jwtUtil.parseToken(accessToken);
                    String email = claims.getSubject();
                    Long userId = Long.parseLong(claims.get("id", String.class));
                    String name = claims.get("name", String.class);
                    String role = claims.get("role", String.class);

                    User userDetails = new User();
                    userDetails.setUserId(userId);
                    userDetails.setName(name);
                    userDetails.setEmail(email);
                    userDetails.setRole(Role.valueOf(role));

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // ── Wrap request and inject headers
                    MutableHttpServletRequest mutable = new MutableHttpServletRequest(request);
                    mutable.removeHeader("Authorization");           // remove raw JWT
                    mutable.putHeader("X-User-Id", String.valueOf(userId));
                    mutable.putHeader("X-User-Name", name);
                    mutable.putHeader("X-User-Email", email);
                    mutable.putHeader("X-User-Role", role);
                    mutable.putHeader("X-Gateway-Auth", "true");

                    // IMPORTANT: pass mutable, not the original request
                    filterChain.doFilter(mutable, response);
                    return;
                } else throw new BadCredentialsException("Invalid token! Token is expired.");
            } catch (RuntimeException e) {
                logger.error(e.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        e.getMessage(),
                        LocalDateTime.now());
                sendErrorResponse(response, 401, errorResponse);
                return;
            } catch (Exception e) {
                logger.error(e.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Something went wrong",
                        e.getMessage(),
                        LocalDateTime.now());
                sendErrorResponse(response, 500, errorResponse);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, ErrorResponse errorResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(errorResponse));

    }
}
