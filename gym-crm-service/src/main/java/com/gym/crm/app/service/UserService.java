package com.gym.crm.app.service;

import com.gym.crm.app.entity.User;

public interface UserService {

    User findById(Long id);

    User findByUsername(String username);

    User save(User user);

    User prepareUserForSave(User user, String password);

    void update(User user);

    void deleteById(Long id);
}
