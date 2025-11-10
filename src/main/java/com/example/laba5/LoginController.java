package com.example.laba5;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void initialize() {
        // Автоматически вызывается после загрузки FXML
        System.out.println(" LoginController инициализирован!");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Попытка входа: " + username + "/" + password);

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля");
            return;
        }

        // Временная проверка - всегда успешный вход
        showAlert("Успех", "Добро пожаловать, " + username + "!");
    }

    @FXML
    private void handleRegister() {
        showAlert("Регистрация", "Функция регистрации будет добавлена");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}