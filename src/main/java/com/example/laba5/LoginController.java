package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private UserManager userManager = UserManager.getInstance();

    @FXML
    private void initialize() {
        // Настраиваем поведение полей ввода
        setupField(usernameField, "Введите логин");
        setupField(passwordField, "Введите пароль");

        // Автоматический вход при нажатии Enter
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());
    }

    private void setupField(TextField field, String placeholder) {
        field.setPromptText(placeholder);

        // Очистка поля при первом клике если там placeholder
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && field.getText().equals(placeholder)) {
                field.setText("");
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Заполните все поля", "Пожалуйста, введите логин и пароль");
            return;
        }

        if (userManager.login(username, password)) {
            User currentUser = userManager.getCurrentUser();

            try {
                String fxmlFile = currentUser.isAdmin()
                        ? "/com/example/laba5/admin_dashboard.fxml"
                        : "/com/example/laba5/user_dashboard.fxml";

                Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 1000, 700));
                stage.setTitle(currentUser.isAdmin() ? "Панель администратора - ТурЭкскурс" : "Панель пользователя - ТурЭкскурс");

            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Ошибка", "Не удалось загрузить приложение: " + e.getMessage());
            }

        } else {
            showAlert(AlertType.ERROR, "Ошибка входа", "Неверный логин или пароль. Проверьте введенные данные.");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Регистрация - ТурЭкскурс");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось открыть форму регистрации");
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Добавляем иконку в алерт
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/example/laba5/icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            // Иконка не обязательна для алертов
        }

        alert.showAndWait();
    }
}