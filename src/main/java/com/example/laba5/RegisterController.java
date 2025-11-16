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

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private UserManager userManager = UserManager.getInstance();

    @FXML
    private void initialize() {
        setupField(usernameField, "Введите логин");
        setupField(passwordField, "Введите пароль");
        setupField(confirmPasswordField, "Подтвердите пароль");
    }

    private void setupField(TextField field, String prompt) {
        field.setPromptText(prompt);
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Валидация
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Ошибка", "Заполните все поля");
            return;
        }

        if (username.length() < 3) {
            showAlert(AlertType.ERROR, "Ошибка", "Логин должен содержать минимум 3 символа");
            return;
        }

        if (password.length() < 4) {
            showAlert(AlertType.ERROR, "Ошибка", "Пароль должен содержать минимум 4 символа");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Ошибка", "Пароли не совпадают");
            return;
        }

        if (userManager.register(username, password, UserRole.USER)) {
            showAlert(AlertType.INFORMATION, "Успех", "Регистрация прошла успешно!\nТеперь вы можете войти в систему.");
            handleBackToLogin();
        } else {
            showAlert(AlertType.ERROR, "Ошибка", "Пользователь с таким логином уже существует");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось вернуться к входу: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}