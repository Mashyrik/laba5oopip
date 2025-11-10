package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class UserDashboardController {

    @FXML private Label welcomeLabel;

    private UserManager userManager = UserManager.getInstance();

    @FXML
    private void initialize() {
        if (userManager.getCurrentUser() != null) {
            welcomeLabel.setText("Добро пожаловать, " + userManager.getCurrentUser().getUsername() + "!");
        }
    }

    @FXML
    private void handleViewExcursions() {
        try {
            // Переходим к просмотру экскурсий
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/user_excursions.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось открыть просмотр экскурсий: " + e.getMessage());
        }
    }

    @FXML
    private void handleCalculateCost() {
        showAlert(AlertType.INFORMATION, "Расчет стоимости",
                "Функция расчета стоимости доступна в разделе 'Просмотр экскурсий'\n\n" +
                        "Перейдите в раздел просмотра для расчета стоимости конкретных экскурсий");
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