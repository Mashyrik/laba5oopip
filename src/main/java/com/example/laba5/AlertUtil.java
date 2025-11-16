package com.example.laba5;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertUtil {

    public static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Добавляем иконку в зависимости от типа алерта
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            String iconPath = getIconPath(type);
            Image icon = new Image(AlertUtil.class.getResourceAsStream(iconPath));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Иконка для алерта не найдена: " + e.getMessage());
        }

        alert.showAndWait();
    }

    private static String getIconPath(AlertType type) {
        switch (type) {
            case INFORMATION:
                return "/com/example/laba5/info.png";    // Инфо
            case WARNING:
                return "/com/example/laba5/warning.png"; // Предупреждение
            case ERROR:
                return "/com/example/laba5/error.png";   // Ошибка
            case CONFIRMATION:
                return "/com/example/laba5/confirm.png"; // Подтверждение
            default:
                return "/com/example/laba5/info.png";    // По умолчанию
        }
    }
}