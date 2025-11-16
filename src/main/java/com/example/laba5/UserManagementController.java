package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;

public class UserManagementController {

    @FXML private TableView<User> usersTable;

    private UserManager userManager = UserManager.getInstance();
    private ObservableList<User> usersData;

    @FXML
    private void initialize() {
        initializeTable();
        loadUsersData();
    }

    private void initializeTable() {
        // Настраиваем колонки таблицы
        TableColumn<User, String> usernameColumn = (TableColumn<User, String>) usersTable.getColumns().get(0);
        TableColumn<User, String> roleColumn = (TableColumn<User, String>) usersTable.getColumns().get(1);
        TableColumn<User, String> statusColumn = (TableColumn<User, String>) usersTable.getColumns().get(2);
        TableColumn<User, String> actionsColumn = (TableColumn<User, String>) usersTable.getColumns().get(3);

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // ТОЛЬКО cellFactory для колонки статуса
        statusColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    User user = getTableRow().getItem();
                    if (user.isBlocked()) {
                        setText("🚫 Заблокирован");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setText("✅ Активен");
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Отключаем сортировку
        usersTable.setSortPolicy(param -> false);

        // Настраиваем колонку действий
        actionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(""));

        actionsColumn.setCellFactory(column -> new TableCell<User, String>() {
            private final HBox buttonContainer = new HBox(5);
            private final Button blockButton = new Button("Заблокировать");
            private final Button unblockButton = new Button("Разблокировать");

            {
                blockButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px;");
                unblockButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 12px;");

                blockButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleBlockUser(user);
                });

                unblockButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleUnblockUser(user);
                });

                buttonContainer.getChildren().addAll(blockButton, unblockButton);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    // Показываем соответствующие кнопки в зависимости от статуса
                    blockButton.setVisible(!user.isBlocked());
                    unblockButton.setVisible(user.isBlocked());
                    setGraphic(buttonContainer);
                }
            }
        });

        usersData = FXCollections.observableArrayList();
        usersTable.setItems(usersData);
    }

    private void loadUsersData() {
        usersData.clear();
        usersData.addAll(userManager.getUsers());
        System.out.println("Загружено пользователей: " + usersData.size());
    }

    private void handleBlockUser(User user) {
        if (userManager.blockUser(user.getUsername())) {
            showAlert(AlertType.INFORMATION, "Успех", "Пользователь " + user.getUsername() + " заблокирован");
            loadUsersData(); // Обновляем таблицу
        } else {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось заблокировать пользователя");
        }
    }

    private void handleUnblockUser(User user) {
        if (userManager.unblockUser(user.getUsername())) {
            showAlert(AlertType.INFORMATION, "Успех", "Пользователь " + user.getUsername() + " разблокирован");
            loadUsersData(); // Обновляем таблицу
        } else {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось разблокировать пользователя");
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsersData();
        showAlert(AlertType.INFORMATION, "Обновление", "Список пользователей обновлен");
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/admin_dashboard.fxml"));
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось вернуться: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        AlertUtil.showAlert(type, title, message);
    }
}