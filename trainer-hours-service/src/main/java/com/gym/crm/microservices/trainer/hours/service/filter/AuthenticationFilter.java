package com.gym.crm.microservices.trainer.hours.service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.trainer.hours.service.rest.exception.ErrorCode;
import com.gym.crm.microservices.trainer.hours.service.rest.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Order(3)
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String INVALID_HEADERS = "Invalid headers";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String username = request.getHeader("X-Username");
        String roles = request.getHeader("X-Roles");

        if (isNull(username) || isNull(roles)) {
            writeUnauthorizedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_HEADERS.getCode(), INVALID_HEADERS);

        String json = objectMapper.writeValueAsString(error);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
