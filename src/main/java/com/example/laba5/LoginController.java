package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private UserManager userManager = UserManager.getInstance();

    @FXML
    private void initialize() {
        System.out.println("✅ LoginController инициализирован!");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        System.out.println("Попытка входа: " + username + "/" + password);

        // Валидация
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Ошибка", "Заполните все поля");
            return;
        }

        // Настоящая аутентификация
        if (userManager.login(username, password)) {
            User currentUser = userManager.getCurrentUser();
            System.out.println("✅ Успешный вход! Пользователь: " + currentUser.getUsername());
            System.out.println("Роль: " + currentUser.getRole());

            showAlert(AlertType.INFORMATION, "Успех",
                    "Добро пожаловать, " + currentUser.getUsername() + "!\n" +
                            "Ваша роль: " + (currentUser.isAdmin() ? "Администратор" : "Пользователь"));

            // Очищаем поля после успешного входа
            usernameField.clear();
            passwordField.clear();

        } else {
            System.out.println("❌ Ошибка входа");
            showAlert(AlertType.ERROR, "Ошибка входа",
                    "Неверный логин или пароль\n\n" +
                            "Тестовые данные:\n" +
                            "Админ - admin/admin123\n" +
                            "Пользователь - user/user123");
        }
    }

    @FXML
    private void handleRegister() {
        showAlert(AlertType.INFORMATION, "Регистрация",
                "Функция регистрации будет добавлена в следующем шаге\n\n" +
                        "Пока используйте тестовые аккаунты:\n" +
                        "• admin / admin123\n" +
                        "• user / user123");
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}