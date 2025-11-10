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

    @FXML
    private void initialize() {
        System.out.println("✅ AdminDashboardController инициализирован!");
        if (userManager.getCurrentUser() != null) {
            welcomeLabel.setText("Добро пожаловать, " + userManager.getCurrentUser().getUsername() + "!");
        }
    }

    @FXML
    private void handleExcursions() {
        showAlert("Управление экскурсиями", "Здесь будет управление экскурсиями");
    }

    @FXML
    private void handleUsers() {
        showAlert("Управление пользователями", "Здесь будет управление пользователями");
    }

    @FXML
    private void handleLogout() {
        try {
            userManager.logout();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}