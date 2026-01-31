package com.he180773.testreact.service;



import com.he180773.testreact.entity.User;
import com.he180773.testreact.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User userOpt = userRepository.findByUsername(username);

        if (userOpt==null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User user = userOpt;

        // ✅ Tạo `UserDetails` để Spring Security sử dụng
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Mật khẩu đã mã hóa
                .roles("USER") // Gán quyền mặc định "USER"
                .build();
    }

    // ✅ Hàm mã hóa mật khẩu trước khi lưu
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // ✅ Hàm kiểm tra mật khẩu nhập vào với mật khẩu trong DB
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
