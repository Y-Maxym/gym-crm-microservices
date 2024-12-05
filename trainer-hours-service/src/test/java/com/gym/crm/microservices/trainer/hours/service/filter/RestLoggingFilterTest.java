package com.gym.crm.microservices.trainer.hours.service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class RestLoggingFilterTest {

    @Mock
    private ObjectMapper objectMapper;

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

        request.setMethod(method);
        request.setRequestURI(uri);
        request.setContent(body.getBytes());

        given(objectMapper.readValue(any(byte[].class), eq(Object.class)))
                .willReturn(new Object());
        given(objectMapper.writeValueAsString(any()))
                .willReturn(body);


        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        ArgumentCaptor<CustomContentCachingRequestWrapper> requestCaptor = forClass(CustomContentCachingRequestWrapper.class);
        ArgumentCaptor<ContentCachingResponseWrapper> responseCaptor = forClass(ContentCachingResponseWrapper.class);

        verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());
        assertThat(requestCaptor.getValue()).isNotNull();
        assertThat(responseCaptor.getValue()).isNotNull();
    }
}