package com.gym.crm.app.security;

import com.gym.crm.app.exception.AuthenticationException;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.rest.exception.ErrorCode;
import com.gym.crm.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";

    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return retrieveUserByUsername(username);
    }

    private UserDetails retrieveUserByUsername(String username) {
        try {
            return userService.findByUsername(username);
        } catch (EntityValidationException e) {
            throw new AuthenticationException(INVALID_USERNAME_OR_PASSWORD, ErrorCode.INVALID_USERNAME_OR_PASSWORD.getCode(), e);
        }
    }
}
