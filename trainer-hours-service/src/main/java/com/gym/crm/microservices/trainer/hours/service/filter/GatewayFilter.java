package com.gym.crm.microservices.trainer.hours.service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Objects.isNull;

@Component
@Order(1)
public class GatewayFilter extends OncePerRequestFilter {

    private static final String GATEWAY_HEADER = "X-Request-Gateway";
    private static final String EXPECTED_GATEWAY_VALUE = "Gateway";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String gatewayHeader = request.getHeader(GATEWAY_HEADER);

        if (isInvalidHeader(gatewayHeader)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isInvalidHeader(String header) {
        return isNull(header) || !EXPECTED_GATEWAY_VALUE.equals(header);
    }
}
