package com.gym.crm.app.filter;

import com.gym.crm.app.logging.MessageHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.gym.crm.app.util.Constants.INFO_TRANSACTION_FILTER_END;
import static com.gym.crm.app.util.Constants.INFO_TRANSACTION_FILTER_START;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private final MessageHelper messageHelper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);

        try {
            log.info(messageHelper.getMessage(INFO_TRANSACTION_FILTER_START, transactionId));
            filterChain.doFilter(request, response);
            log.info(messageHelper.getMessage(INFO_TRANSACTION_FILTER_END, transactionId));
        } finally {
            MDC.clear();
        }
    }
}
