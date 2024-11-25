package com.gym.crm.app.actuator;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorTest {

    @Mock
    private HikariDataSource dataSource;

    @Mock
    private HikariPoolMXBean poolMXBean;

    @Mock
    private Connection connection;

    @InjectMocks
    private DatabaseHealthIndicator databaseHealthIndicator;

    @Test
    @DisplayName("Health check should return UP when database is connected")
    void whenDatabaseIsConnected_thenHealthShouldBeUP() throws SQLException {
        // given
        given(dataSource.getConnection()).willReturn(connection);
        given(dataSource.getHikariPoolMXBean()).willReturn(poolMXBean);
        given(dataSource.getHikariPoolMXBean().getActiveConnections()).willReturn(5);
        given(dataSource.getHikariPoolMXBean().getTotalConnections()).willReturn(10);

        // when
        Health.Builder builder = Health.up();
        databaseHealthIndicator.doHealthCheck(builder);

        // then
        Health health = builder.build();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails().get("database")).isEqualTo("Connected");
        assertThat(health.getDetails().get("activeConnections")).isEqualTo(5);
        assertThat(health.getDetails().get("totalConnections")).isEqualTo(10);
    }

    @Test
    @DisplayName("Health check should return DOWN when database is disconnected")
    void whenDatabaseIsDisconnected_thenHealthShouldBeDOWN() throws SQLException {
        // given
        given(dataSource.getConnection()).willThrow(new SQLException("Connection error"));

        // when
        Health.Builder builder = Health.down();
        databaseHealthIndicator.doHealthCheck(builder);

        Health health = builder.build();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().get("database")).isEqualTo("Disconnected");
        assertThat(health.getDetails().get("error")).isEqualTo("Connection error");
    }
}