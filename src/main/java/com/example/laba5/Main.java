package com.example.laba5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем стартовый экран входа
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/login.fxml"));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Экскурсионная студия");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("✅ Приложение запущено успешно!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}