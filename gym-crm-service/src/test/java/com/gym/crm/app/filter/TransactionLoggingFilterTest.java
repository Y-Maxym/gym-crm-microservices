package com.gym.crm.app.filter;

import com.gym.crm.app.logging.MessageHelper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionLoggingFilterTest {

    @Mock
    private MessageHelper messageHelper;

    @InjectMocks
    private TransactionLoggingFilter filter;

    @Test
    @DisplayName("Test filter functionality")
    void whenDoFilterInternal_thenSuccess() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        MockedStatic<MDC> staticMock = mockStatic(MDC.class);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        staticMock.verify(() -> MDC.put(anyString(), anyString()));
        staticMock.verify(MDC::clear);
        verify(filterChain).doFilter(request, response);
        verify(messageHelper, times(2)).getMessage(anyString(), any());

        staticMock.close();
    }

}