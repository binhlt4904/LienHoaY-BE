package com.he180773.testreact.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "[User]")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "USERNAME", length = 50, nullable = false, unique = true)
    private String username;
    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;
    @Column(name = "ROLE", length = 255, nullable = false)
    private String role;
    @Column(name = "AVATAR", length = 255, nullable = false)
    private String avatar;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    @Column(name = "PHONE", length = 10, nullable = false)
    private String phone;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "Gender")
    private boolean gender;
    @Column(name = "Dob")
    private Date dob;
    @Column(name = "Status")
    private boolean status;
    @Column(name = "verified")
    private boolean verified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
