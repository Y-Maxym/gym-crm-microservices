package com.gym.crm.microservices.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.authservice.filter.AuthenticationFilter;
import com.gym.crm.microservices.authservice.filter.LoginAttemptFilter;
import com.gym.crm.microservices.authservice.filter.RefreshTokenFilter;
import com.gym.crm.microservices.authservice.filter.RestLoggingFilter;
import com.gym.crm.microservices.authservice.rest.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.UNAUTHORIZED_ERROR;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
    private static final List<String> EXCLUDED_URLS = List.of("/api/v1/login", "/api/v1/refresh", "/api/v1/validate");

    private final RestLoggingFilter restLoggingFilter;
    private final RefreshTokenFilter refreshTokenFilter;
    private final AuthenticationFilter authenticationFilter;
    private final LoginAttemptFilter loginAttemptFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EXCLUDED_URLS.toArray(new String[0])).permitAll()
                        .requestMatchers("/swagger-ui", "/v1/api-docs").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(((request, response, authException) ->
                                authenticationFailureHandler().onAuthenticationFailure(request, response, authException))))
                .addFilterBefore(restLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginAttemptFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(refreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_ERROR.getCode(), UNAUTHORIZED_MESSAGE);

            String json = objectMapper.writeValueAsString(error);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(json);
        };
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
}
