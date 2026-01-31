package com.he180773.testreact.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "[Otpverification]")
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "OTP")
    private String otp;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "used")
    private Boolean used;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }
}
