package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.User;
import com.patika.bootcamp.taskmanagement.repository.UserRepository;
import com.patika.bootcamp.taskmanagement.service.UserService;
import com.patika.bootcamp.taskmanagement.exception.UserNotFoundException;
import com.patika.bootcamp.taskmanagement.util.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "PROJECT_MANAGER", "TEAM_LEADER", "TEAM_MEMBER");
    private static final String DEFAULT_ROLE = "TEAM_MEMBER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByDeletedFalse();
    }

    @Override
    public User findById(Long id) {
        Long requiredId = requireId(id, "User id");

        return userRepository.findByIdAndDeletedFalse(requiredId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User details are required");
        }

        validateAndPrepareForSave(user, false);
        return userRepository.save(user);
    }

    @Override
    public User register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User details are required");
        }

        user.setRole(DEFAULT_ROLE);
        validateAndPrepareForSave(user, true);
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User updatedUser) {
        if (updatedUser == null) {
            throw new IllegalArgumentException("User details are required");
        }
        if (!StringUtils.hasText(updatedUser.getName())) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!StringUtils.hasText(updatedUser.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        User existingUser = findById(id);
        String updatedEmail = updatedUser.getEmail().trim();
        if (!existingUser.getEmail().equalsIgnoreCase(updatedEmail) && userRepository.existsByEmail(updatedEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }
        existingUser.setName(updatedUser.getName().trim());
        existingUser.setEmail(updatedEmail);

        if (SecurityUtils.hasRole("ADMIN") && StringUtils.hasText(updatedUser.getRole())) {
            existingUser.setRole(normalizeAndValidateRole(updatedUser.getRole()));
        }

        if (StringUtils.hasText(updatedUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void softDelete(Long id) {
        User user = findById(id);
        user.softDelete();
        userRepository.save(user);
    }

    private void validateAndPrepareForSave(User user, boolean forceDefaultRole) {
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (!StringUtils.hasText(user.getName())) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        String normalizedEmail = user.getEmail().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (forceDefaultRole) {
            user.setRole(DEFAULT_ROLE);
        } else {
            user.setRole(normalizeAndValidateRole(user.getRole()));
        }

        user.setName(user.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDeleted(false);
    }

    private String normalizeAndValidateRole(String role) {
        String normalizedRole = StringUtils.hasText(role) ? role.trim().toUpperCase() : DEFAULT_ROLE;
        if (!ALLOWED_ROLES.contains(normalizedRole)) {
            throw new IllegalArgumentException("Invalid role value");
        }
        return normalizedRole;
    }

    private Long requireId(Long id, String fieldName) {
        return Objects.requireNonNull(id, fieldName + " is required");
    }
}

