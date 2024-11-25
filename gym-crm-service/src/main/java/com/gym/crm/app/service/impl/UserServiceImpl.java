package com.gym.crm.app.service.impl;

import com.gym.crm.app.entity.User;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.service.UserService;
import com.gym.crm.app.service.common.EntityValidator;
import com.gym.crm.app.service.common.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gym.crm.app.rest.exception.ErrorCode.USER_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.rest.exception.ErrorCode.USER_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_USER_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_USER_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.util.Constants.WARN_USER_WITH_ID_NOT_FOUND;
import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final MessageHelper messageHelper;
    private final UserProfileService userProfileService;
    private final EntityValidator entityValidator;

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        entityValidator.checkId(id);

        return repository.findById(id)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_USER_WITH_ID_NOT_FOUND, id), USER_WITH_ID_NOT_FOUND.getCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        entityValidator.checkEntity(username);

        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_USER_WITH_USERNAME_NOT_FOUND, username), USER_WITH_USERNAME_NOT_FOUND.getCode()));
    }

    @Override
    @Transactional
    public User save(User user) {
        entityValidator.checkEntity(user);

        String password = userProfileService.generatePassword();

        User preparedUser = prepareUserForSave(user, password);

        user = repository.save(preparedUser);

        return user.toBuilder().password(password).build();
    }

    @Override
    public User prepareUserForSave(User user, String password) {
        if (isNull(user.getUsername())) {
            String username = userProfileService.generateUsername(user.getFirstName(), user.getLastName());
            user = user.toBuilder().username(username).build();
        }

        if (isNull(user.getPassword())) {
            String hashedPassword = userProfileService.hashPassword(password);
            user = user.toBuilder().password(hashedPassword).build();
        }

        return user;
    }

    @Override
    @Transactional
    public void update(User user) {
        entityValidator.checkEntity(user);
        entityValidator.checkId(user.getId());

        repository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        entityValidator.checkId(id);

        if (repository.findById(id).isEmpty()) {
            log.warn(messageHelper.getMessage(WARN_USER_WITH_ID_NOT_FOUND, id));
        }

        repository.deleteById(id);
    }
}
