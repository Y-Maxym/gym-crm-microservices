package com.gym.crm.microservices.gateway.service.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.gateway.service.security.exception.ErrorResponse;
import com.gym.crm.microservices.gateway.service.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import reactor.core.publisher.Mono;

import static com.gym.crm.microservices.gateway.service.security.exception.ErrorCode.UNAUTHORIZED_ERROR;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(auth -> auth
                        .anyExchange().permitAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint()))
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, exception) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_ERROR.getCode(), UNAUTHORIZED_MESSAGE);

            try {
                String json = objectMapper.writeValueAsString(error);

                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(json.getBytes())));
            } catch (Exception e) {
                return Mono.error(e);
            }
        };
    }
}
