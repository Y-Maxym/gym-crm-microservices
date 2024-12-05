package com.gym.crm.microservices.gateway.service.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionLoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);

        exchange.getRequest().mutate()
                .header("X-Transaction-Id", transactionId)
                .build();

        log.info("Starting request processing with Transaction ID: {}", transactionId);

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    exchange.getResponse().getHeaders().add("X-Transaction-Id", transactionId);
                    log.info("Successfully processed request with Transaction ID: {}", transactionId);
                    MDC.clear();
                });
    }
}
