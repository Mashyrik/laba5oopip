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
        users.add(new User("admin", "admin123", UserRole.ADMIN));
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = loadFromFile();
            if (instance == null) {
                instance = new UserManager();
            }
        }
        return instance;
    }

    public boolean register(String username, String password, UserRole role) {
        if (findUser(username) != null) {
            return false;
        }
        users.add(new User(username, password, role));
        saveToFile();
        return true;
    }

    public boolean login(String username, String password) {
        User user = findUser(username);
        if (user != null && user.getPassword().equals(password) && !user.isBlocked()) {
            currentUser = user;
            return true;
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

    public boolean blockUser(String username) {
        User user = findUser(username);
        if (user != null && user != currentUser) {
            user.setBlocked(true);
            saveToFile();
            return true;
        }
        return false;
    }

    public boolean unblockUser(String username) {
        User user = findUser(username);
        if (user != null) {
            user.setBlocked(false);
            saveToFile();
            return true;
        }
        return false;
    }

    public boolean deleteUser(String username) {
        User user = findUser(username);
        if (user != null && user != currentUser) {
            users.remove(user);
            saveToFile();
            return true;
        }
        return false;
    }

    private User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения пользователей: " + e.getMessage());
        }
    }

    private static UserManager loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))) {
            return (UserManager) ois.readObject();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка загрузки пользователей: " + e.getMessage());
            return null;
        }
    }
}