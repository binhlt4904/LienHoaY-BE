package com.he180773.testreact.service;

import com.he180773.testreact.entity.User;
import com.he180773.testreact.jwt.JwtUtils;
import com.he180773.testreact.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public UserService(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    public User getAccountFromToken(String token) {
        System.out.println(token);
        String username = jwtUtils.extractUsername(token);
        return userRepository.findByUsername(username);
    }
    public void save(User user) {
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(true);
        user.setAvatar("/images/avatar.png");
        userRepository.save(user);
    }
    public User findByUsername(String username) {
        if(userRepository.findByUsername(username)==null){
            System.out.println("user not found");
            return null;
        }
        else{
            return userRepository.findByUsername(username);
        }
    }
}
