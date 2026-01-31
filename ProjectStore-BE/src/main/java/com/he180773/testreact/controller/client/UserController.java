package com.he180773.testreact.controller.client;

import com.he180773.testreact.entity.User;
import com.he180773.testreact.repository.UserRepository;
import com.he180773.testreact.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        System.out.println("user: " + user.isStatus());
        System.out.println("email: " + user.getEmail());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user) {
        Optional<User> optional = userRepository.findById(userId);
        if (!optional.isPresent()) return ResponseEntity.notFound().build();

        User userUpdate = optional.get();
        userUpdate.setName(user.getName());
        userUpdate.setPhone(user.getPhone());
        userUpdate.setEmail(user.getEmail());
        userUpdate.setGender(user.isGender());
        userUpdate.setDob(user.getDob());
        userUpdate.setAvatar(user.getAvatar());

        userRepository.save(userUpdate);
        return ResponseEntity.ok(userUpdate);
    }

}
