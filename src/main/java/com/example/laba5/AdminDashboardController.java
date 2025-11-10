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
    private ExcursionStudio studio = new ExcursionStudio();

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
            // Переходим к управлению экскурсиями
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/excursion_management.fxml.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось открыть управление экскурсиями: " + e.getMessage());
        }
    }

    @FXML
    private void handleUsers() {
        showAlert(AlertType.INFORMATION, "Управление пользователями",
                "Здесь будет управление пользователями:\n" +
                        "• Блокировка пользователей\n" +
                        "• Разблокировка пользователей\n" +
                        "• Просмотр всех пользователей");
    }

    @FXML
    private void handleLogout() {
        try {
            userManager.logout();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось выйти: " + e.getMessage());
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