package com.he180773.testreact.controller;


import com.he180773.testreact.dto.UserDTO;
import com.he180773.testreact.dto.VerifyOtpRequest;
import com.he180773.testreact.entity.Cart;
import com.he180773.testreact.entity.OtpVerification;
import com.he180773.testreact.entity.Wallet;
import com.he180773.testreact.repository.CartRepository;
import com.he180773.testreact.repository.OtpVerificationRepository;
import com.he180773.testreact.repository.WalletRepository;
import com.he180773.testreact.request.AccountRequest;
import com.he180773.testreact.entity.User;
import com.he180773.testreact.jwt.JwtUtils;
import com.he180773.testreact.repository.UserRepository;
import com.he180773.testreact.service.EmailService;
import com.he180773.testreact.service.OtpService;
import com.he180773.testreact.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;
    private final EmailService emailService;
    private final OtpVerificationRepository otpVerificationRepository;
    private final OtpService otpService;
    private final CartRepository cartRepository;

    public AuthController(UserRepository userRepository, AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils, UserService userService,BCryptPasswordEncoder passwordEncoder,
                          WalletRepository walletRepository, EmailService emailService,
                          OtpVerificationRepository otpVerificationRepository, OtpService otpService,
                          CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
        this.emailService = emailService;
        this.otpVerificationRepository = otpVerificationRepository;
        this.otpService = otpService;
        this.cartRepository = cartRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AccountRequest request, HttpServletResponse response) {
        try {
            String input = request.getUsername(); // Có thể là username hoặc email
            String password = request.getPassword();

            User user = userRepository.findByUsername(input);
            if (user == null) {
                user = userRepository.findByEmail(input).orElse(null);
            }

            if (user == null) {
                return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu!");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (!user.isVerified()) {
                return ResponseEntity.status(403).body("Tài khoản chưa xác thực email!");
            }

            String token = jwtUtils.generateToken(user.getUsername(), user.getRole());

            response.setHeader("Set-Cookie",
                    "token=" + token + "; Path=/; HttpOnly; Max-Age=86400; SameSite=Lax");

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setAvatar(user.getAvatar());
            userDTO.setName(user.getName());
            userDTO.setPhone(user.getPhone());
            userDTO.setRole(user.getRole());
            userDTO.setDob(user.getDob());
            userDTO.setGender(user.isGender());
            userDTO.setEmail(user.getEmail());
            userDTO.setStatus(user.isStatus());
            userDTO.setCreatedAt(user.getCreatedAt());

            return ResponseEntity.ok(userDTO);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu!");
        }
    }



    @PostMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@CookieValue(value = "token", required = false) String token,
                                               Model model) {
        User user = userService.getAccountFromToken(token);
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());
            return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AccountRequest request, HttpServletResponse response) {
        try {
            String name = request.getName();
            String username = request.getUsername();
            String password = request.getPassword();

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(400).body("Email đã được sử dụng!");
            }

            if(userRepository.findByUsername(username) != null) {
                return ResponseEntity.status(409).body("Tài khoản đã tồn tại!");
            }

            User user = new User();
            user.setName(name);
            user.setUsername(username);
            user.setEmail(request.getEmail());
            user.setVerified(false);
            if (!password.startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userService.save(user);

            Cart cart = new Cart();
            cart.setUserId(user.getId());
            cart.setCreatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            Wallet  wallet = new Wallet();
            wallet.setUserId(user.getId());
            wallet.setBalance(0);
            wallet.setStatus("ON");
            wallet.setLastUpdated(LocalDateTime.now());
            walletRepository.save(wallet);



            emailService.sendOtp(request.getEmail());
            return ResponseEntity.ok("Đăng ký thành công! Vui lòng kiểm tra email để xác thực OTP.");
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu!");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie jwtCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)  // Xóa token
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok("Logged out successfully!");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest req) {
        Optional<OtpVerification> otpOpt = otpVerificationRepository.findTopByEmailOrderByCreatedAtDesc(req.getEmail());
        if (otpOpt.isEmpty()) return ResponseEntity.badRequest().body("Không tìm thấy OTP");

        OtpVerification otp = otpOpt.get();

        System.out.println("OTP: " + otp.getOtp());
        System.out.println("OTP REQ: " + req.getOtp());

        if (otp.getUsed()) return ResponseEntity.badRequest().body("OTP đã được sử dụng");
        if (!otp.getOtp().trim().equals(req.getOtp().trim())) {
            return ResponseEntity.badRequest().body("OTP không chính xác");
        }

        if (otp.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP đã hết hạn");
        }

        otp.setUsed(true);
        otpVerificationRepository.save(otp);

        // Đánh dấu user là verified
        Optional<User> userOpt = userRepository.findByEmail(req.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            userRepository.save(user);
        }

        return ResponseEntity.ok("Xác thực thành công!");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng nhập username hoặc email.");
        }

        User user = userRepository.findByUsername(email);
        if (user == null) {
            user = userRepository.findByEmail(email).get();
        }
        if (user.isVerified())
            return ResponseEntity.status(400).body("Tài khoản đã xác thực!");

        emailService.sendOtp(user.getEmail());
        return ResponseEntity.ok("OTP đã được gửi lại.");
    }

    @PostMapping("/resend-reset-otp")
    public ResponseEntity<?> resendResetOtp(@RequestParam String email) {
        if (!otpService.canResendOtp(email)) {
            long seconds = otpService.getRemainingTime(email) / 1000;
            return ResponseEntity.status(429).body("Vui lòng chờ " + seconds + " giây để gửi lại mã OTP.");
        }

        String otp = otpService.generateOtp(email);
        emailService.sendResetOtp(email, otp);
        return ResponseEntity.ok("Mã OTP đã được gửi lại.");
    }

    @GetMapping("/get-email")
    public ResponseEntity<?> getEmail(@RequestParam("identifier") String identifier) {
        System.out.println("Identifier: " + identifier);
        User user = userRepository.findByUsername(identifier);
        if (user == null) {
            user = userRepository.findByEmail(identifier).get();
        }

        if (user == null) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }

        System.out.println("User email: " + user.getEmail());

        return ResponseEntity.ok(user.getEmail());
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Email không tồn tại.");
        }

        try {
            String otp = otpService.generateOtp(email);
            emailService.sendResetOtp(email, otp);
            return ResponseEntity.ok("Đã gửi OTP đến email.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(429).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Không thể gửi OTP.");
        }
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");

        if (otpService.verifyOtp(email, otp)) {
            return ResponseEntity.ok("OTP hợp lệ.");
        } else {
            return ResponseEntity.status(400).body("OTP không chính xác hoặc đã hết hạn!");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Email không tồn tại!");
        }

        User user = optionalUser.get();
        if (!newPassword.startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);

        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        // Lấy username từ token đã xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("Người dùng không tồn tại.");
        }


        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(400).body("Mật khẩu hiện tại không đúng.");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Đổi mật khẩu thành công.");
    }









}

