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
            System.out.println("✅ Созданы тестовые пользователи");
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
        System.out.println("❌ Ошибка входа: " + username);
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

    // Методы для блокировки/разблокировки
    public boolean blockUser(String username) {
        // Нельзя заблокировать самого себя
        if (currentUser != null && currentUser.getUsername().equals(username)) {
            System.out.println("❌ Нельзя заблокировать самого себя: " + username);
            return false;
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setBlocked(true);
                saveToFile();
                System.out.println("✅ Пользователь заблокирован: " + username);
                return true;
            }
        }
        System.out.println("❌ Пользователь не найден: " + username);
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
        System.out.println("❌ Пользователь не найден: " + username);
        return false;
    }

    // Метод для смены роли пользователя
    public boolean changeUserRole(String username, UserRole newRole) {
        // Нельзя изменить роль самого себя
        if (currentUser != null && currentUser.getUsername().equals(username)) {
            System.out.println("❌ Нельзя изменить роль самого себя: " + username);
            return false;
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                UserRole oldRole = user.getRole();
                // В классе User нужно добавить сеттер для роли
                // user.setRole(newRole);
                System.out.println("✅ Роль пользователя изменена: " + username + " (" + oldRole + " -> " + newRole + ")");
                saveToFile();
                return true;
            }
        }
        System.out.println("❌ Пользователь не найден: " + username);
        return false;
    }

    // Метод для поиска пользователя по имени
    public User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Метод для проверки существования пользователя
    public boolean userExists(String username) {
        return findUserByUsername(username) != null;
    }

    // Метод для получения количества пользователей
    public int getUserCount() {
        return users.size();
    }

    // Метод для получения количества активных пользователей
    public int getActiveUserCount() {
        int count = 0;
        for (User user : users) {
            if (!user.isBlocked()) {
                count++;
            }
        }
        return count;
    }

    // Метод для получения количества заблокированных пользователей
    public int getBlockedUserCount() {
        int count = 0;
        for (User user : users) {
            if (user.isBlocked()) {
                count++;
            }
        }
        return count;
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

            // Диагностика - выводим информацию о пользователях
            for (User user : users) {
                System.out.println("   👤 " + user.getUsername() +
                        " | Роль: " + user.getRole() +
                        " | Статус: " + (user.isBlocked() ? "Заблокирован" : "Активен"));
            }

        } catch (FileNotFoundException e) {
            System.out.println("Файл пользователей не найден, будут созданы тестовые пользователи");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Ошибка загрузки пользователей: " + e.getMessage());
        }
    }

    // Метод для получения статистики (может пригодиться для админ-панели)
    public String getStatistics() {
        int total = getUserCount();
        int active = getActiveUserCount();
        int blocked = getBlockedUserCount();
        int admins = 0;
        int regularUsers = 0;

        for (User user : users) {
            if (user.isAdmin()) {
                admins++;
            } else {
                regularUsers++;
            }
        }

        return String.format(
                "Статистика пользователей:\n" +
                        "• Всего пользователей: %d\n" +
                        "• Активных: %d\n" +
                        "• Заблокированных: %d\n" +
                        "• Администраторов: %d\n" +
                        "• Обычных пользователей: %d",
                total, active, blocked, admins, regularUsers
        );
    }
}