package com.cts.trialledger.apigateway.config;


import com.cts.trialledger.apigateway.security.CustomAccessDeniedHandler;
import com.cts.trialledger.apigateway.security.JwtFilter;
import com.cts.trialledger.apigateway.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static com.cts.trialledger.apigateway.model.Role.*;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {

    private final CustomUserDetailService userDetailService;
    private final JwtFilter jwtFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(CustomUserDetailService userDetailService, JwtFilter jwtFilter, CustomAccessDeniedHandler accessDeniedHandler) {
        this.userDetailService = userDetailService;
        this.jwtFilter = jwtFilter;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) {
        return security.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/api/users/**").hasAnyRole(ADMIN.name(), PI.name())
                                .requestMatchers("/api/audit/**").hasAnyRole(ADMIN.name(), AUDITOR.name(), COMPLIANCE.name())
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/studies/**").hasAnyRole(ADMIN.name(), PI.name(), COORDINATOR.name(), COMPLIANCE.name(), DATA_MANAGER.name(), AUDITOR.name())
                                .requestMatchers("/api/participants/**", "/api/consents/**").hasAnyRole(ADMIN.name(), PI.name(), COORDINATOR.name(), COMPLIANCE.name(), DATA_MANAGER.name(), AUDITOR.name(), PARTICIPANT.name())
                                .requestMatchers("/api/samples/**").hasAnyRole(ADMIN.name(), PI.name(), COORDINATOR.name(), COMPLIANCE.name(), DATA_MANAGER.name(), AUDITOR.name(), TECHNICIAN.name())
                                .requestMatchers("/api/sourcedata/**", "/api/visits/**").hasAnyRole(ADMIN.name(), PI.name(), COORDINATOR.name(), COMPLIANCE.name(), DATA_MANAGER.name(), PARTICIPANT.name(), TECHNICIAN.name())
                                .requestMatchers("/api/adverse-events/**").hasAnyRole(ADMIN.name(), PI.name(), COORDINATOR.name(), COMPLIANCE.name(), DATA_MANAGER.name(), AUDITOR.name(), TECHNICIAN.name())
                                .requestMatchers("/api/provenance",
                                        "/api/provenance/**",
                                        "/api/audit-packages",
                                        "/api/audit-packages/**",
                                        "/api/dataset-snapshot",
                                        "/api/dataset-snapshot/**")
                                .hasAnyRole(ADMIN.name(), PI.name(), COMPLIANCE.name(), DATA_MANAGER.name(), AUDITOR.name())
                                .requestMatchers("/api/reports/**", "/api/kpis/**").hasAnyRole(ADMIN.name(), PI.name(), COORDINATOR.name(), COMPLIANCE.name(), DATA_MANAGER.name(), AUDITOR.name())
                                .requestMatchers("/api/alerts/**").hasAnyRole(ADMIN.name(), COMPLIANCE.name())
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
