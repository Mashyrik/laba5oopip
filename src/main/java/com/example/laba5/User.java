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
    public String getStatus() {
        return isBlocked() ? "Заблокирован" : "Активен";
    }
    // Сеттеры
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public void setRole(UserRole role) { this.role = role; } // Добавьте этот сеттер

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}