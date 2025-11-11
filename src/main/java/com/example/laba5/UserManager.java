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
        loadFromFile(); // Загружаем при создании

        // Если файла нет, создаем тестовых пользователей
        if (users.isEmpty()) {
            users.add(new User("admin", "admin123", UserRole.ADMIN));
            users.add(new User("user", "user123", UserRole.USER));
            saveToFile();
        }
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
                System.out.println("❌ Регистрация failed: пользователь " + username + " уже существует");
                return false;
            }
        }

        users.add(new User(username, password, role));
        saveToFile(); // Сохраняем после регистрации
        System.out.println("✅ Новый пользователь зарегистрирован: " + username + " (роль: " + role + ")");
        return true;
    }

    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password) &&
                    !user.isBlocked()) {
                currentUser = user;
                System.out.println("✅ Успешный вход: " + username);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("✅ Выход: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    // Методы для блокировки/разблокировки (для админа)
    public boolean blockUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setBlocked(true);
                saveToFile();
                System.out.println("✅ Пользователь заблокирован: " + username);
                return true;
            }
        }
        return false;
    }

    public boolean unblockUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setBlocked(false);
                saveToFile();
                System.out.println("✅ Пользователь разблокирован: " + username);
                return true;
            }
        }
        return false;
    }

    // Сохранение в файл
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            oos.writeObject(this);
            System.out.println("✅ Пользователи сохранены: " + users.size() + " пользователей");
        } catch (IOException e) {
            System.out.println("❌ Ошибка сохранения пользователей: " + e.getMessage());
        }
    }

    // Загрузка из файла
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))) {
            UserManager loaded = (UserManager) ois.readObject();
            this.users = loaded.users;
            System.out.println("✅ Пользователи загружены: " + users.size() + " пользователей");
        } catch (FileNotFoundException e) {
            System.out.println("Файл пользователей не найден, будут созданы тестовые пользователи");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Ошибка загрузки пользователей: " + e.getMessage());
        }
    }
}