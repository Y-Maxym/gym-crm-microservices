package com.gym.crm.app.service.common;

import com.gym.crm.app.exception.ApplicationException;
import com.gym.crm.app.rest.exception.FieldError;
import com.gym.crm.app.util.function.TriFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BindingResultsServiceTest {

    @Mock
    private TriFunction<String, Integer, List<FieldError>, ApplicationException> exceptionFunction;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private BindingResultsService bindingResultsService;

    @Test
    @DisplayName("Test handle with errors functionality")
    void givenBindingResultHasErrors_whenHandle_thenExceptionIsThrown() {
        // given
        String errorMessage = "error message";
        Integer errorCode = 1;
        org.springframework.validation.FieldError fieldError = new org.springframework.validation.FieldError("objectName", "field", "defaultMessage");
        ApplicationException exception = mock(ApplicationException.class);

        given(bindingResult.hasErrors())
                .willReturn(true);
        given(bindingResult.getFieldErrors())
                .willReturn(List.of(fieldError));
        given(exceptionFunction.apply(any(), any(), any()))
                .willReturn(exception);

        // when
        assertThrows(ApplicationException.class, () ->
                bindingResultsService.handle(bindingResult, exceptionFunction, errorMessage, errorCode));

        // then
        verify(exceptionFunction).apply(eq(errorMessage), eq(errorCode), any());
    }

    @Test
    @DisplayName("Test handle without errors functionality")
    void givenBindingResultHasNoErrors_whenHandle_thenNoExceptionIsThrown() {
        // given
        String errorMessage = "error message";
        Integer errorCode = 1;

        given(bindingResult.hasErrors())
                .willReturn(false);

        // when
        bindingResultsService.handle(bindingResult, exceptionFunction, errorMessage, errorCode);

        // then
        verify(exceptionFunction, never()).apply(eq(errorMessage), eq(errorCode), any());
    }
}