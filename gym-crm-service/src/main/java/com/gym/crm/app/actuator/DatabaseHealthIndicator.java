package com.gym.crm.app.actuator;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator extends AbstractHealthIndicator {

    private final HikariDataSource dataSource;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                int activeConnections = dataSource.getHikariPoolMXBean().getActiveConnections();
                int totalConnections = dataSource.getHikariPoolMXBean().getTotalConnections();

                builder.up()
                        .withDetail("database", "Connected")
                        .withDetail("activeConnections", activeConnections)
                        .withDetail("totalConnections", totalConnections);
            }
        } catch (SQLException e) {
            builder.down()
                    .withDetail("database", "Disconnected")
                    .withException(e)
                    .withDetail("error", e.getMessage());
        }
    }
}
