package com.he180773.testreact.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpire = new ConcurrentHashMap<>();
    private final long OTP_EXPIRATION = 5 * 60 * 1000; // 5 phút

    public String generateOtp(String email) {
        long currentTime = System.currentTimeMillis();
        Long expireTime = otpExpire.get(email);
        System.out.println("Expiration time for " + email + ": " + expireTime);
        System.out.println("current time: " + currentTime);

        if (expireTime != null && currentTime < expireTime) {
            throw new IllegalStateException("Vui lòng đợi 5 phút trước khi yêu cầu mã OTP mới.");
        }


        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 số
        otpStorage.put(email, otp);
        otpExpire.put(email, currentTime + OTP_EXPIRATION);

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        System.out.println("Verifying OTP for email: " + email + ", OTP: " + otp);
        System.out.println("Current time: " + System.currentTimeMillis());
        System.out.println("Storage" + " contents: " + otpStorage);
        System.out.println("Expiration time for " + email + ": " + otpExpire.get(email));
        if (!otpStorage.containsKey(email)) return false;

        long expireTime = otpExpire.getOrDefault(email, 0L);
        if (System.currentTimeMillis() > expireTime) {
            otpStorage.remove(email);
            otpExpire.remove(email);
            return false;
        }

        boolean isValid = otpStorage.get(email).equals(otp);
        if (isValid) {
            otpStorage.remove(email);
            otpExpire.remove(email);
        }
        return isValid;
    }

    public boolean canResendOtp(String email) {
        Long expireTime = otpExpire.get(email);
        return expireTime == null || System.currentTimeMillis() > expireTime;
    }

    public long getRemainingTime(String email) {
        return otpExpire.containsKey(email)
                ? Math.max(otpExpire.get(email) - System.currentTimeMillis(), 0)
                : 0;
    }
}

