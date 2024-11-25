package com.gym.crm.app.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MetricsFilterTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter requestCounter;

    @Mock
    private Timer requestTimer;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private MetricsFilter filter;

    @BeforeEach
    void setUp() {
        given(meterRegistry.counter("http_requests_total")).willReturn(requestCounter);
        given(meterRegistry.timer("http_request_duration_seconds")).willReturn(requestTimer);

        filter = new MetricsFilter(meterRegistry);
    }

    @Test
    @DisplayName("Test request count increment and timing")
    void whenDoFilterInternal_thenMetricsAreRecorded() {
        // given
        doNothing().when(requestCounter).increment();
        doNothing().when(requestTimer).record(any(Runnable.class));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(requestCounter).increment();
        verify(requestTimer).record(any(Runnable.class));
    }

}