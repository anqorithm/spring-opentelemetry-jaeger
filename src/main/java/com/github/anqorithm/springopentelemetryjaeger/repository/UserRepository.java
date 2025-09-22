package com.github.anqorithm.springopentelemetryjaeger.repository;

import com.github.anqorithm.springopentelemetryjaeger.annotation.Traced;
import com.github.anqorithm.springopentelemetryjaeger.mapper.UserMapper;
import com.github.anqorithm.springopentelemetryjaeger.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Traced
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    public Optional<User> findById(String id) {
        User user = userMapper.findById(id);
        return Optional.ofNullable(user);
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public List<User> findByName(String name) {
        return userMapper.findByName(name);
    }

    public Optional<User> findByEmail(String email) {
        User user = userMapper.findByEmail(email);
        return Optional.ofNullable(user);
    }

    public User save(User user) {
        int rowsAffected = userMapper.insert(user);
        if (rowsAffected <= 0) {
            throw new RuntimeException("Failed to save user");
        }
        return user;
    }

    public User update(User user) {
        int rowsAffected = userMapper.update(user);
        if (rowsAffected <= 0) {
            throw new RuntimeException("User not found for update");
        }
        return user;
    }

    public boolean deleteById(String id) {
        int rowsAffected = userMapper.deleteById(id);
        return rowsAffected > 0;
    }

    public long count() {
        return userMapper.count();
    }
}