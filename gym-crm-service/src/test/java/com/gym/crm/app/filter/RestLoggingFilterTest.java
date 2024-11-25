package com.gym.crm.app.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.logging.MessageHelper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static com.gym.crm.app.util.Constants.INFO_REST_LOGGING_FILTER_REQUEST;
import static com.gym.crm.app.util.Constants.INFO_REST_LOGGING_FILTER_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class RestLoggingFilterTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MessageHelper messageHelper;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RestLoggingFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test filter functionality")
    void whenDoFilterInternal_thenSuccess() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String method = "POST";
        String uri = "/api/test";
        String body = "{\"username\":\"user\",\"password\":\"pass\"}";
        String hiddenBody = "{\"username\":\"****\",\"password\":\"****\"}";
        String transactionId = "1";
        MDC.put("transactionId", transactionId);

        request.setMethod(method);
        request.setRequestURI(uri);
        request.setContent(body.getBytes());

        given(objectMapper.readValue(any(byte[].class), eq(Object.class)))
                .willReturn(new Object());
        given(objectMapper.writeValueAsString(any()))
                .willReturn(body);

        given(messageHelper.getMessage(eq(INFO_REST_LOGGING_FILTER_REQUEST), anyString(), anyString(), anyString(), anyString()))
                .willReturn("Request log message");
        given(messageHelper.getMessage(eq(INFO_REST_LOGGING_FILTER_RESPONSE), anyString(), anyString(), anyString()))
                .willReturn("Response log message");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        ArgumentCaptor<CustomContentCachingRequestWrapper> requestCaptor = forClass(CustomContentCachingRequestWrapper.class);
        ArgumentCaptor<ContentCachingResponseWrapper> responseCaptor = forClass(ContentCachingResponseWrapper.class);

        verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());
        assertThat(requestCaptor.getValue()).isNotNull();
        assertThat(responseCaptor.getValue()).isNotNull();

        verify(messageHelper).getMessage(eq(INFO_REST_LOGGING_FILTER_REQUEST), eq(method), eq(uri), eq(hiddenBody), eq(transactionId));
        verify(messageHelper).getMessage(eq(INFO_REST_LOGGING_FILTER_RESPONSE), eq(200), eq(""), eq(transactionId));
    }
}