package com.gym.crm.app.actuator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MemoryHealthIndicatorTest {

    @InjectMocks
    private MemoryHealthIndicator memoryHealthIndicator;


    @Test
    @DisplayName("Health check should return UP when memory is sufficient")
    void whenMemoryIsSufficient_thenHealthShouldBeUP() {
        // when
        Health.Builder builder = Health.up();
        memoryHealthIndicator.doHealthCheck(builder);

        // then
        Health health = builder.build();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails().get("freeMemory")).isNotNull();
        assertThat(health.getDetails().get("totalMemory")).isNotNull();
        assertThat(health.getDetails().get("maxMemory")).isNotNull();
        assertThat(health.getDetails().get("usedMemory")).isNotNull();
        assertThat(health.getDetails().get("availableMemory")).isNotNull();
        assertThat(health.getDetails().get("memoryUsage")).isNotNull();
    }
}