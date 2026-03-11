package com.he180773.testreact.service;

import com.he180773.testreact.entity.OtpVerification;
import com.he180773.testreact.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmailService {

    private final OtpVerificationRepository otpRepo;

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    private final String FROM_EMAIL = "leventshop@gmail.com";
    private final String FROM_NAME = "Levents";

    public EmailService(OtpVerificationRepository otpRepo) {
        this.otpRepo = otpRepo;
    }

    public void sendOtp(String email) {

        String otp = String.valueOf(new Random().nextInt(899999) + 100000);

        OtpVerification entity = new OtpVerification();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUsed(false);

        otpRepo.save(entity);

        sendEmail(email, "Mã xác thực đăng ký tài khoản", otp);
    }

    public void sendResetOtp(String email, String otp) {
        sendEmail(email, "Mã xác nhận đặt lại mật khẩu", otp);
    }

    private void sendEmail(String to, String subject, String otp) {

        RestTemplate restTemplate = new RestTemplate();

        String html =
                "<div style='font-family:Arial;max-width:500px;margin:auto;padding:20px;border:1px solid #eee;border-radius:10px'>" +
                        "<h2>Xác thực tài khoản - Levents</h2>" +
                        "<p>Mã OTP của bạn:</p>" +
                        "<h1 style='color:red'>" + otp + "</h1>" +
                        "<p>Mã có hiệu lực trong 5 phút.</p>" +
                        "</div>";

        Map<String, Object> body = new HashMap<>();

        Map<String, String> sender = new HashMap<>();
        sender.put("email", FROM_EMAIL);
        sender.put("name", FROM_NAME);

        Map<String, String> toEmail = new HashMap<>();
        toEmail.put("email", to);

        body.put("sender", sender);
        body.put("to", List.of(toEmail));
        body.put("subject", subject);
        body.put("htmlContent", html);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(
                "https://api.brevo.com/v3/smtp/email",
                request,
                String.class
        );
    }
}