package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;

    private UserManager userManager = UserManager.getInstance();
    private ExcursionStudio studio = ExcursionStudio.getInstance();

    @FXML
    private void initialize() {
        studio.loadFromFile();
        if (userManager.getCurrentUser() != null) {
            welcomeLabel.setText("Добро пожаловать, " + userManager.getCurrentUser().getUsername() + "!");
        }
        System.out.println("Загружено экскурсий: " + studio.getExcursions().size());
    }

    @FXML
    private void handleExcursions() {
        try {
            System.out.println("Пытаемся загрузить excursion_management.fxml...");

            java.net.URL fxmlUrl = getClass().getResource("/com/example/laba5/excursion_management.fxml");
            System.out.println("URL файла: " + fxmlUrl);

            if (fxmlUrl == null) {
                System.out.println("❌ Файл не найден! Проверь путь.");
                showAlert(AlertType.ERROR, "Ошибка", "Файл excursion_management.fxml не найден!");
                return;
            }

            System.out.println("✅ Файл найден, загружаем...");
            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            System.out.println("❌ Ошибка загрузки: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось открыть управление экскурсиями: " + e.getMessage());
        }
    }

    @FXML
    private void handleUsers() {
        try {
            System.out.println("Переход к управлению пользователями...");
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/user_management.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Управление пользователями");
        } catch (Exception e) {
            System.out.println("❌ Ошибка загрузки управления пользователями: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось открыть управление пользователями: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            userManager.logout();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось выйти: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        AlertUtil.showAlert(type, title, message);
    }
}