package com.example.laba5;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private UserRole role;
    private boolean blocked;

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.blocked = false;
    }

    // Геттеры
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public boolean isBlocked() { return blocked; }

    // Сеттеры
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}