package com.gym.crm.microservices.authservice.service;

import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.exception.EntityValidationException;
import com.gym.crm.microservices.authservice.repository.UserRepository;
import com.gym.crm.microservices.authservice.service.common.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.USER_WITH_USERNAME_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String ERROR_USER_WITH_USERNAME_NOT_FOUND = "User with username %s not found";

    private final UserRepository repository;
    private final EntityValidator entityValidator;


    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        entityValidator.checkEntity(username);

        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityValidationException(ERROR_USER_WITH_USERNAME_NOT_FOUND.formatted(username), USER_WITH_USERNAME_NOT_FOUND.getCode()));
    }
}
