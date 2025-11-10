package com.example.laba5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Пробуем загрузить FXML
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/login.fxml"));
            Scene scene = new Scene(root, 600, 400);
            primaryStage.setTitle("Экскурсионная студия");
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("✅ Приложение запущено успешно!");

        } catch (Exception e) {
            System.out.println("❌ Ошибка загрузки FXML: " + e.getMessage());
            e.printStackTrace();

            // Создаем простой интерфейс на случай ошибки
            createSimpleInterface(primaryStage);
        }
    }

    private void createSimpleInterface(Stage stage) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(
                "Экскурсионная студия\n\n" +
                        "Файл login.fxml не найден!\n" +
                        "Создай файл login.fxml в папке:\n" +
                        "src/main/resources/com/example/laba5/"
        );
        label.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(label);
        Scene scene = new Scene(root, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}