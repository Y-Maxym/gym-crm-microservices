package com.gym.crm.microservices.authservice.service;

import com.gym.crm.microservices.authservice.exception.AuthenticationException;
import com.gym.crm.microservices.authservice.exception.EntityPersistException;
import com.gym.crm.microservices.authservice.model.UserCredentials;
import com.gym.crm.microservices.authservice.rest.exception.ErrorCode;
import com.gym.crm.microservices.authservice.service.common.BindingResultsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.AUTHENTICATION_ERROR;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";

    private final AuthenticationManager authenticationManager;
    private final BindingResultsService bindingResultsService;

    @Transactional(readOnly = true)
    public Authentication authenticate(UserCredentials credentials, BindingResult bindingResult) {
        bindingResultsService.handle(bindingResult, EntityPersistException::new, "Authentication error", AUTHENTICATION_ERROR.getCode());

        String username = credentials.getUsername();
        String password = credentials.getPassword();

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            return authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new AuthenticationException(INVALID_USERNAME_OR_PASSWORD, ErrorCode.INVALID_USERNAME_OR_PASSWORD.getCode(), e);
        }
    }
}
