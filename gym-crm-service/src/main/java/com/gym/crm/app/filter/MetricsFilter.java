package com.gym.crm.app.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MetricsFilter extends OncePerRequestFilter {

    private final Counter requestCounter;
    private final Timer requestTimer;

    @Autowired
    public MetricsFilter(MeterRegistry registry) {
        this.requestCounter = registry.counter("http_requests_total");
        this.requestTimer = registry.timer("http_request_duration_seconds");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        requestCounter.increment();
        requestTimer.record(() -> {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
