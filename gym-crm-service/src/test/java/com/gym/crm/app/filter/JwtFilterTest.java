package com.gym.crm.app.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.rest.exception.ErrorCode;
import com.gym.crm.app.rest.model.ErrorResponse;
import com.gym.crm.app.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    private static final String AUTHORIZATION = "Bearer valid.token.here";
    private static final String TOKEN = "valid.token.here";
    private static final String USERNAME = "testUser";

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private JwtFilter jwtFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Test valid token functionality")
    void givenUsername_whenDoFilterInternal_thenSuccess() throws Exception {
        // given
        UserDetails userDetails = mock(UserDetails.class);

        given(request.getHeader("Authorization"))
                .willReturn(AUTHORIZATION);
        given(jwtService.extractAccessToken(AUTHORIZATION))
                .willReturn(TOKEN);
        given(jwtService.extractUsername(TOKEN))
                .willReturn(USERNAME);
        given(jwtService.isPresentValidAccessToken(AUTHORIZATION))
                .willReturn(true);
        given(userDetailsService.loadUserByUsername(USERNAME))
                .willReturn(userDetails);
        given(jwtService.isValid(TOKEN, USERNAME))
                .willReturn(true);
        given(request.getRequestURI())
                .willReturn("/url");

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Test invalid token functionality")
    void givenUsername_whenDoFilterInternal_thenExpiredToken() throws Exception {
        // given
        UserDetails userDetails = mock(UserDetails.class);

        given(request.getHeader("Authorization"))
                .willReturn(AUTHORIZATION);
        given(jwtService.extractUsername(TOKEN))
                .willReturn(USERNAME);
        given(userDetailsService.loadUserByUsername(USERNAME))
                .willReturn(userDetails);
        given(jwtService.isValid(TOKEN, USERNAME))
                .willThrow(new SignatureException("invalid token", null));
        given(response.getWriter())
                .willReturn(printWriter);
        given(jwtService.isPresentValidAccessToken(AUTHORIZATION))
                .willReturn(true);
        given(request.getRequestURI())
                .willReturn("/url");
        given(jwtService.extractAccessToken(AUTHORIZATION))
                .willReturn(TOKEN);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response.getWriter()).write(messageCaptor.capture());

        ErrorResponse errorResponse = new ObjectMapper().readValue(messageCaptor.getValue(), ErrorResponse.class);
        assertThat(ErrorCode.INVALID_ACCESS_TOKEN.getCode()).isEqualTo(errorResponse.getCode());
    }

    @Test
    @DisplayName("Test invalid token functionality")
    void givenUsername_whenDoFilterInternal_thenExpired() throws Exception {
        // given
        UserDetails userDetails = mock(UserDetails.class);

        given(request.getHeader("Authorization"))
                .willReturn(AUTHORIZATION);
        given(jwtService.extractUsername(TOKEN))
                .willReturn(USERNAME);
        given(userDetailsService.loadUserByUsername(USERNAME))
                .willReturn(userDetails);
        given(jwtService.isValid(TOKEN, USERNAME))
                .willThrow(new ExpiredJwtException(null, null, "token expired"));
        given(response.getWriter())
                .willReturn(printWriter);
        given(jwtService.isPresentValidAccessToken(AUTHORIZATION))
                .willReturn(true);
        given(request.getRequestURI())
                .willReturn("/url");
        given(jwtService.extractAccessToken(AUTHORIZATION))
                .willReturn(TOKEN);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response.getWriter()).write(messageCaptor.capture());

        ErrorResponse errorResponse = new ObjectMapper().readValue(messageCaptor.getValue(), ErrorResponse.class);
        assertThat(ErrorCode.EXPIRED_ACCESS_TOKEN.getCode()).isEqualTo(errorResponse.getCode());
    }
}