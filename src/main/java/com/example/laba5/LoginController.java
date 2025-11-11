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

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Ошибка", "Заполните все поля");
            return;
        }

        if (userManager.login(username, password)) {
            User currentUser = userManager.getCurrentUser();
            System.out.println("✅ Успешный вход! Пользователь: " + currentUser.getUsername());

            try {
                String fxmlFile = currentUser.isAdmin()
                        ? "/com/example/laba5/admin_dashboard.fxml"
                        : "/com/example/laba5/user_dashboard.fxml";

                System.out.println("Пытаемся загрузить: " + fxmlFile);

                Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle(currentUser.isAdmin() ? "Панель администратора" : "Панель пользователя");

            } catch (Exception e) {
                System.out.println("❌ Ошибка загрузки: " + e.getMessage());
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Ошибка", "Ошибка загрузки: " + e.getMessage());
            }

        } else {
            showAlert(AlertType.ERROR, "Ошибка входа", "Неверный логин или пароль");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            System.out.println("Переход к регистрации...");
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 450));
            stage.setTitle("Регистрация");
        } catch (Exception e) {
            System.out.println("❌ Ошибка загрузки формы регистрации: " + e.getMessage());
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось открыть форму регистрации: " + e.getMessage());
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