package com.gym.crm.microservices.authservice.service.common;

import com.gym.crm.microservices.authservice.exception.EntityValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class EntityValidatorTest {

    @InjectMocks
    private EntityValidator validator;

    @Test
    @DisplayName("Test check null entity functionality")
    public void givenNullEntity_whenCheckEntity_thenExceptionIsThrows() {
        // when & then
        assertThatThrownBy(() -> validator.checkEntity(null))
                .isInstanceOf(EntityValidationException.class)
                .hasMessage("Entity cannot be null");
    }

    @Test
    @DisplayName("Test check valid entity functionality")
    public void givenValidEntity_whenCheckEntity_thenSuccess() {
        // given
        Object entity = new Object();

        // when
        validator.checkEntity(entity);

        // then
        assertDoesNotThrow(() -> validator.checkEntity(entity));
    }
}
