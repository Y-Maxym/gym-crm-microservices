package com.gym.crm.microservices.authservice.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Service
public class LoginAttemptService {

    private final Map<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    @Value("${login.block.duration:5m}")
    private Duration blockDuration;

    @Value("${login.block.maxAttempts:3}")
    private int maxAttempts;

    public void loginFailed(String username) {
        AttemptInfo attemptInfo = attemptsCache.getOrDefault(username, new AttemptInfo(0, null));
        attemptInfo.incrementAttempts();

        long blockDurationSeconds = blockDuration.toSeconds();
        if (attemptInfo.getAttempts() >= maxAttempts) {
            attemptInfo.setBlockTime(LocalDateTime.now().plusSeconds(blockDurationSeconds));
        }

        attemptsCache.put(username, attemptInfo);
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public boolean isBlocked(String username) {
        AttemptInfo attemptInfo = attemptsCache.get(username);
        if (isNull(attemptInfo) || isNull(attemptInfo.getBlockTime())) {
            return false;
        }

        if (attemptInfo.getBlockTime().isAfter(LocalDateTime.now())) {
            return true;
        }

        attemptsCache.remove(username);
        return false;
    }

    @Getter
    @Setter
    private static class AttemptInfo {

        private int attempts;
        private LocalDateTime blockTime;

        public AttemptInfo(int attempts, LocalDateTime blockTime) {
            this.attempts = attempts;
            this.blockTime = blockTime;
        }

        public void incrementAttempts() {
            this.attempts++;
        }

    }
}