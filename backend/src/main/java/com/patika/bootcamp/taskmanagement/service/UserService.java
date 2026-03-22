package com.patika.bootcamp.taskmanagement.service;

import com.patika.bootcamp.taskmanagement.model.User;
import com.patika.bootcamp.taskmanagement.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User findById(Long id) throws UserNotFoundException;

    User create(User user);

    User register(User user);

    User update(Long id, User user) throws UserNotFoundException;

    void softDelete(Long id) throws UserNotFoundException;
}

