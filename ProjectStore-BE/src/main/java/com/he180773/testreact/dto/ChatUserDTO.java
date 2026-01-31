package com.he180773.testreact.dto;


import java.time.LocalDateTime;

public class ChatUserDTO {
    private String userId;
    private String userName;
    private String avatar;
    private String lastMessage;
    private LocalDateTime lastSentAt;

    public ChatUserDTO() {
    }

    public ChatUserDTO(String userId, String userName, String avatar, String lastMessage, LocalDateTime lastSentAt) {
        this.userId = userId;
        this.userName = userName;
        this.avatar = avatar;
        this.lastMessage = lastMessage;
        this.lastSentAt = lastSentAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(LocalDateTime lastSentAt) {
        this.lastSentAt = lastSentAt;
    }
}

