package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.User;
import com.patika.bootcamp.taskmanagement.repository.UserRepository;
import com.patika.bootcamp.taskmanagement.service.UserService;
import com.patika.bootcamp.taskmanagement.service.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByDeletedFalse(); // ✅ Sadece aktif kullanıcılar listeleniyor.
    }

    @Override
    public User findById(Long id) {
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User updatedUser) {
        User existingUser = findById(id);
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        return userRepository.save(existingUser);
    }

    @Override
    public void softDelete(Long id) {
        User user = findById(id);
        user.setDeleted(true); // ✅ Soft delete işlemi
        userRepository.save(user);
    }
}
