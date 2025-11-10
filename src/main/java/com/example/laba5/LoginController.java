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
        usernameField.setPromptText("Введите логин");
        passwordField.setPromptText("Введите пароль");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Ошибка", "Заполните все поля");
            return;
        }

        if (userManager.login(username, password)) {
            try {
                String fxmlFile = userManager.getCurrentUser().isAdmin() ?
                        "/com/example/excursionstudio/admin_dashboard.fxml" :
                        "/com/example/excursionstudio/user_dashboard.fxml";

                Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));

            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Ошибка", "Не удалось загрузить интерфейс: " + e.getMessage());
            }
        } else {
            showAlert(AlertType.ERROR, "Ошибка входа", "Неверный логин или пароль");
        }
    }

    @FXML
    private void handleRegister() {
        showAlert(AlertType.INFORMATION, "Регистрация", "Функция в разработке");
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}