package com.gym.crm.app.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.security.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginAttemptFilterTest {

    private static final String TOO_MANY_ATTEMPTS_ERROR_MESSAGE = "Account is temporarily blocked due to too many failed login attempts";
    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private LoginAttemptFilter filter;

    @Test
    @DisplayName("Test successful login attempt functionality")
    void whenLoginSucceeded_thenLoginAttemptServiceCalled() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String username = "user";
        String body = "{\"username\":\"" + username + "\"}";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);

        request.setMethod("POST");
        request.setRequestURI("/api/v1/login");
        request.setContent(body.getBytes(StandardCharsets.UTF_8));

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        CustomContentCachingRequestWrapper requestWrapper = new CustomContentCachingRequestWrapper(request);

        given(objectMapper.readValue(any(byte[].class), any(TypeReference.class)))
                .willReturn(requestBody);

        // when
        filter.doFilterInternal(requestWrapper, responseWrapper, filterChain);

        // then
        verify(loginAttemptService).loginSucceeded(username);
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @DisplayName("Test failed login attempt functionality")
    void whenLoginFailed_thenLoginAttemptServiceCalled() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String username = "user";
        String body = "{\"username\":\"" + username + "\"}";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);

        request.setMethod("POST");
        request.setRequestURI("/api/v1/login");
        request.setContent(body.getBytes(StandardCharsets.UTF_8));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        CustomContentCachingRequestWrapper requestWrapper = new CustomContentCachingRequestWrapper(request);

        given(objectMapper.readValue(any(byte[].class), any(TypeReference.class)))
                .willReturn(requestBody);

        // when
        filter.doFilterInternal(requestWrapper, responseWrapper, filterChain);

        // then
        verify(loginAttemptService).loginFailed(username);
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @DisplayName("Test blocked account due to too many failed login attempts")
    void whenAccountIsBlocked_thenForbiddenResponse() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String username = "user";
        String body = "{\"username\":\"" + username + "\"}";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);

        request.setMethod("POST");
        request.setRequestURI("/api/v1/login");
        request.setContent(body.getBytes(StandardCharsets.UTF_8));

        CustomContentCachingRequestWrapper requestWrapper = new CustomContentCachingRequestWrapper(request);

        given(loginAttemptService.isBlocked(username))
                .willReturn(true);
        given(objectMapper.readValue(any(byte[].class), any(TypeReference.class)))
                .willReturn(requestBody);

        // when
        filter.doFilterInternal(requestWrapper, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(response.getContentAsString()).contains(TOO_MANY_ATTEMPTS_ERROR_MESSAGE);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Test username extraction from request")
    void whenRequestContainsUsername_thenExtractUsername() throws IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String username = "user";
        String body = "{\"username\":\"user\"}";
        request.setContent(body.getBytes(StandardCharsets.UTF_8));

        CustomContentCachingRequestWrapper wrapper = new CustomContentCachingRequestWrapper(request);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);

        given(objectMapper.readValue(any(byte[].class), any(TypeReference.class)))
                .willReturn(requestBody);

        // when
        Object actual = ReflectionTestUtils.invokeMethod(filter, "getUsername", wrapper);

        // then
        assertThat(actual).isEqualTo("user");
    }
}