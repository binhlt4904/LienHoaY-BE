package com.he180773.testreact.service;

import com.he180773.testreact.entity.OtpVerification;
import com.he180773.testreact.repository.OtpVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final OtpVerificationRepository otpRepo;

    private final String FROM_EMAIL = "leventshop@gmail.com"; // 👈 sửa thành email bạn cấu hình
    private final String FROM_NAME = "Levents";

    public EmailService(JavaMailSender mailSender, OtpVerificationRepository otpRepo) {
        this.mailSender = mailSender;
        this.otpRepo = otpRepo;
    }
    public void sendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);

        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpEntity.setUsed(false);
        otpRepo.save(otpEntity);

        String subject = "Mã xác thực đăng ký tài khoản";
        String text = "Mã OTP của bạn là: " + otp;

        sendEmail(email, subject, text);
    }

    public void sendResetOtp(String toEmail, String otp) {
        String subject = "Mã xác nhận đặt lại mật khẩu";
        String text = "Mã OTP của bạn là: " + otp + ". Mã sẽ hết hạn trong 5 phút.";
        sendEmail(toEmail, subject, text);
    }

    private void sendEmail(String to, String subject, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Nội dung HTML
            String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 20px; " +
                    "border: 1px solid #e0e0e0; border-radius: 10px; background-color: #fafafa;\">" +
                    "<h2 style=\"color: #2e7d32;\">Xác thực tài khoản - Levents</h2>" +
                    "<p>Xin chào,</p>" +
                    "<p>Chúng tôi đã nhận được yêu cầu xác thực của bạn. Đây là mã OTP của bạn:</p>" +
                    "<div style=\"font-size: 24px; font-weight: bold; color: #d32f2f; margin: 20px 0;\">" + otp + "</div>" +
                    "<p>Mã sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                    "<hr style=\"margin: 20px 0;\">" +
                    "<p style=\"font-size: 12px; color: #757575;\">Email được gửi tự động từ hệ thống Levents. Vui lòng không trả lời email này.</p>" +
                    "</div>";

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true để gửi dưới dạng HTML
            helper.setFrom(FROM_EMAIL, FROM_NAME);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }


}
