package com.gym.crm.app.security;

import com.gym.crm.app.exception.AuthenticationException;
import com.gym.crm.app.rest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";

    private final AuthenticationManager authenticationManager;

    @Transactional(readOnly = true)
    public Authentication authenticate(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            return authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new AuthenticationException(INVALID_USERNAME_OR_PASSWORD, ErrorCode.INVALID_USERNAME_OR_PASSWORD.getCode(), e);
        }
    }
}
