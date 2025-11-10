package com.example.laba5;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements Serializable {
    private static UserManager instance;
    private List<User> users;
    private User currentUser;

    private UserManager() {
        users = new ArrayList<>();
        // Создаем администратора по умолчанию
        users.add(new User("admin", "admin123", UserRole.ADMIN));
        // И тестового пользователя
        users.add(new User("user", "user123", UserRole.USER));
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public boolean register(String username, String password, UserRole role) {
        // Проверяем, нет ли уже такого пользователя
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }

        users.add(new User(username, password, role));
        return true;
    }

    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password) &&
                    !user.isBlocked()) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }
}