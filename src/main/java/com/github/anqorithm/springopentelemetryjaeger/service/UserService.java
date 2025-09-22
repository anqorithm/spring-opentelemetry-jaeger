package com.github.anqorithm.springopentelemetryjaeger.service;

import com.github.anqorithm.springopentelemetryjaeger.annotation.Traced;
import com.github.anqorithm.springopentelemetryjaeger.model.User;
import com.github.anqorithm.springopentelemetryjaeger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Traced
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(String id) {
        validateUserId(id);
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return userRepository.findByName(name);
    }

    public User createUser(String name, String email) {
        validateUserData(name, email);

        if (email != null && !email.isEmpty()) {
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        String userId = UUID.randomUUID().toString();

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User updateUser(String id, String name, String email) {
        validateUserId(id);
        validateUserData(name, email);

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User existingUser = existingUserOpt.get();
        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.update(existingUser);
    }

    public boolean deleteUser(String id) {
        validateUserId(id);
        return userRepository.deleteById(id);
    }

    @Traced(operation = "validate-user-id")
    private void validateUserId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }

    @Traced(operation = "validate-user-data")
    private void validateUserData(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name is required");
        }

        if (email != null && !email.isEmpty() && !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}