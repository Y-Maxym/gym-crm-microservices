package com.gym.crm.app.service.common;

import com.gym.crm.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private static final String USERNAME_TEMPLATE = "%s.%s";

    private final PasswordUtils passwordUtils;
    private final UserRepository repository;

    public String generatePassword() {
        return passwordUtils.generatePassword();
    }

    public String hashPassword(String password) {
        return passwordUtils.hashPassword(password);
    }

    public boolean isPasswordCorrect(String inputPassword, String storedPassword) {
        return passwordUtils.isPasswordCorrect(inputPassword, storedPassword);
    }

    @Transactional(readOnly = true)
    public String generateUsername(String firstName, String lastName) {
        String username = USERNAME_TEMPLATE.formatted(firstName, lastName);

        return isDuplicatedUsername(username)
                ? addSerialNumberToUsername(username)
                : username;
    }

    private boolean isDuplicatedUsername(String username) {
        return repository.findByUsername(username).isPresent();
    }

    private String addSerialNumberToUsername(String username) {
        int serialNumber = repository.findAllByUsernameContains(username).size();

        return username + serialNumber;
    }
}
