package com.gym.crm.app.actuator;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class MemoryHealthIndicator extends AbstractHealthIndicator {

    private static final long MEMORY_THRESHOLD = 50 * 1024 * 1024;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long usedMemory = totalMemory - freeMemory;
        long availableMemory = maxMemory - usedMemory;

        double memoryUsage = (double) usedMemory / availableMemory * 100;

        if (availableMemory > MEMORY_THRESHOLD) {
            builder.up()
                    .withDetail("freeMemory", formatMemory(freeMemory))
                    .withDetail("totalMemory", formatMemory(totalMemory))
                    .withDetail("maxMemory", formatMemory(maxMemory))
                    .withDetail("usedMemory", formatMemory(usedMemory))
                    .withDetail("availableMemory", formatMemory(availableMemory))
                    .withDetail("memoryUsage", String.format("%.2f%%", memoryUsage));
        } else {
            builder.down()
                    .withDetail("freeMemory", formatMemory(freeMemory))
                    .withDetail("totalMemory", formatMemory(totalMemory))
                    .withDetail("maxMemory", formatMemory(maxMemory))
                    .withDetail("usedMemory", formatMemory(usedMemory))
                    .withDetail("availableMemory", formatMemory(availableMemory))
                    .withDetail("memoryUsage", String.format("%.2f%%", memoryUsage))
                    .withDetail("error", "Low available memory");
        }
    }

    private String formatMemory(long memory) {
        return String.format("%d MB", memory / (1024 * 1024));
    }
}
